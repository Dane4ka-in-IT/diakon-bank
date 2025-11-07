package com.diakonbank.financialpulse.service.service;

import com.diakonbank.commondto.*;
import com.diakonbank.financialpulse.service.client.DiakonHelpClient;
import com.diakonbank.financialpulse.service.entity.Account;
import com.diakonbank.financialpulse.service.entity.Transaction;
import com.diakonbank.financialpulse.service.mapper.AccountMapper;
import com.diakonbank.financialpulse.service.mapper.TransactionMapper;
import com.diakonbank.financialpulse.service.repository.AccountRepository;
import com.diakonbank.financialpulse.service.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal todayExpenses = todayTransactions.stream()
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .map(Transaction::getAmount)
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
