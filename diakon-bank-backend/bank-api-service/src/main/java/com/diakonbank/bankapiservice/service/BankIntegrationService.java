package com.diakonbank.bankapiservice.service;

import com.diakonbank.bankapiservice.client.BankApiClient;
import com.diakonbank.bankapiservice.dto.response.*;
import com.diakonbank.bankapiservice.entity.Account;
import com.diakonbank.bankapiservice.entity.Transaction;
import com.diakonbank.bankapiservice.exception.BankIntegrationException;
import com.diakonbank.bankapiservice.repository.AccountRepository;
import com.diakonbank.bankapiservice.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    public void syncUserData(Long userId) {
        log.info("Starting data synchronization for user {}", userId);
        try {
            String accessToken = bankApiClient.getAccessToken().block();
            String consentId = bankApiClient.getConsent(accessToken).block();
            List<BankAcountDTO> bankAccounts = bankApiClient.getAccounts(accessToken, consentId).block();

            if (bankAccounts == null) {
                log.warn("Bank accounts list is null, aborting sync for user {}", userId);
                return;
            }
            log.info("Fetched {} accounts for user {}", bankAccounts.size(), userId);
            for (BankAcountDTO bankAccountDto : bankAccounts) {
                Account account = processAccount(bankAccountDto, userId);
                updateAccountBalance(account, accessToken, consentId);
                syncTransactionsForAccount(account, accessToken, consentId);
            }
        } catch (Exception e) {
            log.error("Error during data synchronization for user {}", userId, e);
            throw new BankIntegrationException("Failed to synchronize user data", e);
        }
    }

    private Account processAccount(BankAcountDTO bankAccountDto, Long userId) {
        Account account = accountRepository.findByExternalAccountId(bankAccountDto.getAccountId())
                .orElse(new Account());

        account.setUserId(userId);
        account.setExternalAccountId(bankAccountDto.getAccountId());
        account.setNickname(bankAccountDto.getNickname());
        account.setAccountType(bankAccountDto.getAccountType());
        account.setAccountSubType(bankAccountDto.getAccountSubType());
        account.setCurrency(bankAccountDto.getCurrency());
        account.setStatus(bankAccountDto.getStatus());
        Optional.ofNullable(bankAccountDto.getOpeningDate()).ifPresent(date -> account.setOpeningDate(LocalDate.parse(date)));
        Optional.ofNullable(bankAccountDto.getAccount()).flatMap(list -> list.stream().findFirst()).ifPresent(acc -> account.setAccountNumber(acc.getIdentification()));

        return accountRepository.save(account);
    }

    private void updateAccountBalance(Account account, String accessToken, String consentId) {
        log.debug("Updating balance for account {}", account.getExternalAccountId());
        BankBalanceResponseDTO balanceResponse = bankApiClient.getAccountBalances(account.getExternalAccountId(), accessToken, consentId).block();

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
                    accountRepository.save(account);
                    log.info("Updated balance for account {}", account.getExternalAccountId());
                });
    }

    private void syncTransactionsForAccount(Account account, String accessToken, String consentId) {
        log.debug("Syncing transactions for account {}", account.getExternalAccountId());
        String fromDateTime = LocalDateTime.now().minusYears(1).format(DateTimeFormatter.ISO_DATE_TIME);
        String toDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        int currentPage = 1;
        int totalNewTransactionsCount = 0;
        boolean hasMorePages = true;

        while(hasMorePages) {
            BankTransactionResponseDTO transactionResponse = bankApiClient.getTransactionsForAccount(account.getExternalAccountId(), accessToken, consentId, fromDateTime, toDateTime, currentPage).block();

            if (transactionResponse == null || transactionResponse.getData() == null || transactionResponse.getData().getTransaction() == null) {
                log.warn("No transaction data on page {} for account {}. Aborting sync.", currentPage, account.getExternalAccountId());
                break;
            }

            List<BankTransactionDTO> transactions = transactionResponse.getData().getTransaction();
            log.info("Fetched {} transactions for account {} on page {}", transactions.size(), account.getExternalAccountId(), currentPage);

            for (BankTransactionDTO txnDto : transactions) {
                if (transactionRepository.findByExternalTransactionId(txnDto.getTransactionId()).isPresent()) {
                    continue;
                }
                Transaction transaction = new Transaction();
                transaction.setUserId(account.getUserId());
                transaction.setAccount(account);
                transaction.setExternalTransactionId(txnDto.getTransactionId());
                Optional.ofNullable(txnDto.getBookingDateTime()).ifPresent(dt -> transaction.setBookingDateTime(ZonedDateTime.parse(dt).toLocalDateTime()));

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
