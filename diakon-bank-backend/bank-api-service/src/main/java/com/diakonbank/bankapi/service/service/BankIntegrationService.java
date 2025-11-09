package com.diakonbank.bankapi.service.service;
import com.diakonbank.bankapi.service.client.BankApiClient;
import com.diakonbank.bankapi.service.dto.response.*;
import com.diakonbank.bankapi.service.entity.Account;
import com.diakonbank.bankapi.service.entity.Transaction;
import com.diakonbank.bankapi.service.exception.BankIntegrationException;
import com.diakonbank.bankapi.service.repository.AccountRepository;
import com.diakonbank.bankapi.service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Slf4j
public class BankIntegrationService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BankApiClient bankApiClient;
    @Transactional
    public void syncUserData(String login, String password, Long userId, String bank) {
        log.info("Starting data synchronization for user {}", userId);
        try {
            String accessToken = bankApiClient.getAccessToken(login, password, bank).block();
            log.info("Obtained access_token.");
            String consentId = bankApiClient.getConsent(accessToken, login, bank).block();
            log.info("Obtained consent_id: {}.", consentId);
            ConsentDetailsResponse consentDetails = bankApiClient.getConsentDetails(consentId, accessToken, bank).block();
            if (consentDetails == null || consentDetails.getData() == null ||
                    consentDetails.getData().getPermissions() == null || consentDetails.getData().getPermissions().isEmpty()) {
                throw new BankIntegrationException("CRITICAL: Consent created, but NO PERMISSIONS were granted!");
            }
            log.info("SUCCESS! Permissions granted for consent {}: {}", consentId, consentDetails.getData().getPermissions());
            List<BankAcountDTO> bankAccounts = bankApiClient.getAccounts(accessToken, consentId, login, bank).block();
            if (bankAccounts == null || bankAccounts.isEmpty()) {
                log.warn("Bank accounts list is null or empty for user {}. This is unexpected with valid consent.", userId);
                return;
            }
            log.info("Fetched {} accounts for user {}", bankAccounts.size(), userId);
            for (BankAcountDTO bankAccountDto : bankAccounts) {
                Account account = processAccount(bankAccountDto, userId, bank);
                updateAccountBalance(account, accessToken, consentId, login, bank);
                syncTransactionsForAccount(account, accessToken, consentId, login, bank);
            }
        } catch (Exception e) {
            log.error("Error during data synchronization for user {}", userId, e);
            throw new BankIntegrationException("Failed to synchronize user data", e);
        }
    }
    private Account processAccount(BankAcountDTO bankAccountDto, Long currentOwnerUserId, String bank) {
        Account account = accountRepository.findByExternalAccountId(bankAccountDto.getAccountId())
                .orElse(new Account());
        account.setOwnerUserId(currentOwnerUserId);
        account.setBankName(bank);
        account.setUpdatedAt(Instant.now());
        account.setExternalAccountId(bankAccountDto.getAccountId());
        account.setNickname(bankAccountDto.getNickname());
        account.setAccountType(bankAccountDto.getAccountType());
        account.setAccountSubType(bankAccountDto.getAccountSubType());
        account.setCurrency(bankAccountDto.getCurrency());
        account.setStatus(bankAccountDto.getStatus());
        Optional.ofNullable(bankAccountDto.getOpeningDate()).ifPresent(dateStr -> {
            if (dateStr != null && !dateStr.isBlank()) account.setOpeningDate(LocalDate.parse(dateStr));
        });
        Optional.ofNullable(bankAccountDto.getAccount()).flatMap(list -> list.stream().findFirst()).ifPresent(acc -> account.setAccountNumber(acc.getIdentification()));
        return accountRepository.save(account);
    }
    private void updateAccountBalance(Account account, String accessToken, String consentId, String login, String bank) {
        BankBalanceResponseDTO balanceResponse = bankApiClient.getAccountBalances(account.getExternalAccountId(), accessToken, consentId, login, bank).block();
        if (balanceResponse == null || balanceResponse.getData() == null || balanceResponse.getData().getBalance() == null) {
            log.warn("No balance data found for account {}", account.getExternalAccountId());
            return;
        }
        balanceResponse.getData().getBalance().stream()
                .filter(balance -> "InterimAvailable".equals(balance.getType()))
                .findFirst()
                .map(BankBalanceDTO.BalanceItem::getAmount)
                .ifPresent(amountData -> {
                    account.setBalance(new BigDecimal(amountData.getAmount()));
                    account.setUpdatedAt(Instant.now());
                    accountRepository.save(account);
                    log.info("Updated balance for account {}", account.getExternalAccountId());
                });
    }
    private void syncTransactionsForAccount(Account account, String accessToken, String consentId, String login, String bank) {
        String fromDateTime = LocalDateTime.now().minusYears(1).format(DateTimeFormatter.ISO_DATE_TIME);
        String toDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        int currentPage = 1;
        int totalNewTransactionsCount = 0;
        boolean hasMorePages = true;
        while(hasMorePages) {
            BankTransactionResponseDTO transactionResponse = bankApiClient.getTransactionsForAccount(account.getExternalAccountId(), accessToken, consentId, fromDateTime, toDateTime, currentPage, login, bank).block();
            if (transactionResponse == null || transactionResponse.getData() == null || transactionResponse.getData().getTransaction() == null) {
                break;
            }
            List<BankTransactionDTO> transactions = transactionResponse.getData().getTransaction();
            log.info("Fetched {} transactions for account {} on page {}", transactions.size(), account.getExternalAccountId(), currentPage);
            for (BankTransactionDTO txnDto : transactions) {
                if (transactionRepository.findByExternalTransactionId(txnDto.getTransactionId()).isPresent()) {
                    continue;
                }
                Transaction transaction = new Transaction();
                transaction.setOwnerUserId(account.getOwnerUserId());
                transaction.setAccount(account);
                transaction.setExternalTransactionId(txnDto.getTransactionId());
                Optional.ofNullable(txnDto.getBookingDateTime()).ifPresent(dtStr -> {
                    if(dtStr != null && !dtStr.isBlank()) transaction.setBookingDateTime(ZonedDateTime.parse(dtStr).toLocalDateTime());
                });
                Optional.ofNullable(txnDto.getAmount()).ifPresent(amount -> {
                    transaction.setAmount(new BigDecimal(amount.getAmount()));
                    transaction.setCurrency(amount.getCurrency());
                });
                transaction.setCreditDebitIndicator(txnDto.getCreditDebitIndicator());
                transaction.setTransactionInformation(txnDto.getTransactionInformation());
                transaction.setStatus(txnDto.getStatus());
                transactionRepository.save(transaction);
                totalNewTransactionsCount++;
            }
            if (transactionResponse.getMeta() != null && transactionResponse.getMeta().getTotalPages() != null) {
                hasMorePages = currentPage < transactionResponse.getMeta().getTotalPages();
            } else {
                hasMorePages = false;
            }
            currentPage++;
        }
        log.info("Saved a total of {} new transactions for account {}", totalNewTransactionsCount, account.getExternalAccountId());
    }
}