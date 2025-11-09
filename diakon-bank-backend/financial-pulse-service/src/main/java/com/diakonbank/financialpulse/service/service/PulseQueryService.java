package com.diakonbank.financialpulse.service.service;
import com.diakonbank.commondto.*;
import com.diakonbank.financialpulse.service.client.DiakonHelpClient;
import com.diakonbank.financialpulse.service.dto.ai.DiakonHelpRequestDto;
import com.diakonbank.financialpulse.service.dto.ai.DiakonHelpResponseDto;
import com.diakonbank.financialpulse.service.entity.Account;
import com.diakonbank.financialpulse.service.entity.Transaction;
import com.diakonbank.financialpulse.service.mapper.AccountMapper;
import com.diakonbank.financialpulse.service.mapper.TransactionMapper;
import com.diakonbank.financialpulse.service.repository.AccountRepository;
import com.diakonbank.financialpulse.service.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PulseQueryService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final DiakonHelpClient diakonHelpClient;
    private final ObjectMapper objectMapper;
    public DashboardResponseDto getDashboardData(Long ownerUserId) {
        List<Account> accounts = accountRepository.findByOwnerUserId(ownerUserId);
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        List<Transaction> todayTransactions = transactionRepository.findByOwnerUserIdAndBookingDateTimeBetween(ownerUserId, startOfDay, endOfDay);
        List<Transaction> last15Transactions = transactionRepository.findTop15ByOwnerUserIdOrderByBookingDateTimeDesc(ownerUserId);
        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal todayIncome = todayTransactions.stream()
                .map(Transaction::getAmount)
                .filter(amount -> amount.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal todayExpenses = todayTransactions.stream()
                .map(Transaction::getAmount)
                .filter(amount -> amount.compareTo(BigDecimal.ZERO) < 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<AccountDto> accountDtos = accounts.stream()
                .map(AccountMapper::toDto)
                .collect(Collectors.toList());
        List<TransactionDto> transactionDtos = last15Transactions.stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
        return DashboardResponseDto.builder()
                .totalBalance(totalBalance)
                .todayIncome(todayIncome)
                .todayExpenses(todayExpenses)
                .accounts(accountDtos)
                .lastTransactions(transactionDtos)
                .build();
    }
    public List<AccountDto> getAccountsByOwnerId(Long ownerUserId) {
        return accountRepository.findByOwnerUserId(ownerUserId).stream()
                .map(AccountMapper::toDto)
                .collect(Collectors.toList());
    }
    public List<TransactionDto> getTransactionsByOwnerId(Long ownerUserId) {
        return transactionRepository.findByOwnerUserId(ownerUserId).stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
    }
    public DiakonHelpResponseDto askDiakonHelp(Long ownerUserId, String userQuestion) {
        List<Transaction> transactions = transactionRepository.findTop15ByOwnerUserIdOrderByBookingDateTimeDesc(ownerUserId);
        String transactionsJson = serializeTransactionsToJson(transactions);
        DiakonHelpRequestDto request = new DiakonHelpRequestDto();
        request.setUserText(userQuestion);
        request.setTransactionsJson(transactionsJson);
        return diakonHelpClient.getAnalysis(request).block();
    }
    public LeaksResponseDto analyzeForLeaks(Long ownerUserId, int months) {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(months);
        List<Transaction> transactions = transactionRepository.findByOwnerUserIdAndBookingDateTimeAfter(ownerUserId, startDate);
        String transactionsJson = serializeTransactionsToJson(transactions);
        return diakonHelpClient.findLeaks(transactionsJson).block();
    }
    public AnalyticsResponseDto getAnalytics(Long ownerUserId, Optional<LocalDate> fromDate, Optional<LocalDate> toDate) {
        LocalDateTime startDateTime = fromDate.map(LocalDate::atStartOfDay).orElse(LocalDateTime.MIN);
        LocalDateTime endDateTime = toDate.map(date -> date.atTime(LocalTime.MAX)).orElse(LocalDateTime.MAX);
        List<Transaction> transactions = transactionRepository.findByOwnerUserIdAndBookingDateTimeBetween(ownerUserId, startDateTime, endDateTime);
        List<Transaction> incomes = transactions.stream()
                .filter(t -> t.getCreditDebitIndicator().equalsIgnoreCase("Credit"))
                .toList();
        List<Transaction> expenses = transactions.stream()
                .filter(t -> t.getCreditDebitIndicator().equalsIgnoreCase("Debit"))
                .toList();
        BigDecimal totalExpenses = expenses.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).abs();
        Map<String, BigDecimal> expensesByCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getTransactionInformation,
                        Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
        List<CategorySummaryDto> expenseSummaries = expensesByCategory.entrySet().stream()
                .map(entry -> CategorySummaryDto.builder()
                        .category(entry.getKey())
                        .amount(entry.getValue().abs())
                        .percentage(calculatePercentage(entry.getValue().abs(), totalExpenses))
                        .build())
                .sorted(Comparator.comparing(CategorySummaryDto::getAmount).reversed())
                .collect(Collectors.toList());
        BigDecimal totalIncomes = incomes.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, BigDecimal> incomesByCategory = incomes.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getTransactionInformation,
                        Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
        List<CategorySummaryDto> incomeSummaries = incomesByCategory.entrySet().stream()
                .map(entry -> CategorySummaryDto.builder()
                        .category(entry.getKey())
                        .amount(entry.getValue())
                        .percentage(calculatePercentage(entry.getValue(), totalIncomes))
                        .build())
                .sorted(Comparator.comparing(CategorySummaryDto::getAmount).reversed())
                .collect(Collectors.toList());
        return AnalyticsResponseDto.builder()
                .period(formatPeriod(fromDate, toDate))
                .expenses(AnalyticsResponseDto.AggregatedData.builder()
                        .total(totalExpenses)
                        .byCategory(expenseSummaries)
                        .build())
                .incomes(AnalyticsResponseDto.AggregatedData.builder()
                        .total(totalIncomes)
                        .byCategory(incomeSummaries)
                        .build())
                .build();
    }
    private String formatPeriod(Optional<LocalDate> fromDate, Optional<LocalDate> toDate) {
        String from = fromDate.map(LocalDate::toString).orElse("начала времён");
        String to = toDate.map(LocalDate::toString).orElse("текущего момента");
        return "c " + from + " до " + to;
    }
    private BigDecimal calculatePercentage(BigDecimal part, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return part.divide(total, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    private String serializeTransactionsToJson(List<Transaction> transactions) {
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
        try {
            return objectMapper.writeValueAsString(transactionDtos);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize transactions to JSON", e);
        }
    }
}