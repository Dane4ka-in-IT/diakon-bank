package com.diakonbank.bankapiservice.service;

import com.diakonbank.bankapiservice.client.BankApiClient;
import com.diakonbank.commondto.entity.Account;
import com.diakonbank.commondto.entity.Transaction;
import com.diakonbank.bankapiservice.exception.BankIntegrationException;
import com.diakonbank.bankapiservice.repository.AccountRepository;
import com.diakonbank.bankapiservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
            log.info("Obtained access_token for user {}", userId);
            log.debug("Access Token: {}", accessToken);

            String consentId = bankApiClient.getConsent(accessToken).block();
            log.info("Obtained consentId: {}", consentId);

            List<Map<String, Object>> accountsList = bankApiClient.getAccounts(accessToken, consentId).block();
            if (accountsList == null) {
                log.warn("accountsList is null, aborting sync for user {}", userId);
                return;
            }
            log.info("Fetched {} accounts for user {}", accountsList.size(), userId);

            for (Map<String, Object> accMap : accountsList) {
                String externalAccountId = (String) accMap.get("accountId");

                Account account = accountRepository.findByExternalAccountId(externalAccountId)
                        .orElse(new Account());

                account.setUserId(userId);
                account.setExternalAccountId(externalAccountId);
                account.setNickname((String) accMap.get("nickname"));
                account.setAccountType((String) accMap.get("accountType"));
                account.setAccountSubType((String) accMap.get("accountSubType"));
                account.setCurrency((String) accMap.get("currency"));
                account.setStatus((String) accMap.get("status"));
                account.setOpeningDate(LocalDate.parse((String) accMap.get("openingDate")));

                // Extract nested account number
                if (accMap.containsKey("account") && accMap.get("account") instanceof List) {
                    List<Map<String, String>> innerAccountList = (List<Map<String, String>>) accMap.get("account");
                    if (!innerAccountList.isEmpty()) {
                        account.setAccountNumber(innerAccountList.get(0).get("identification"));
                    }
                }
                
                Account savedAccount = accountRepository.save(account);

                updateAccountBalance(savedAccount, accessToken, consentId);
                syncTransactionsForAccount(savedAccount, accessToken, consentId);
            }
        } catch (Exception e) {
            log.error("Error during data synchronization for user " + userId, e);
            throw new BankIntegrationException("Failed to synchronize user data", e);
        }
    }

    private void updateAccountBalance(Account account, String accessToken, String consentId) {
        log.debug("Attempting to update balance for account {}...", account.getExternalAccountId());
        Map<String, Object> balanceResponse = bankApiClient.getAccountBalances(account.getExternalAccountId(), accessToken, consentId).block();

        log.debug("Raw balance response: {}", balanceResponse);

        if (balanceResponse != null && balanceResponse.containsKey("data")) {
            Map<String, Object> data = (Map<String, Object>) balanceResponse.get("data");
            if (data != null && data.containsKey("balance")) {
                List<Map<String, Object>> balances = (List<Map<String, Object>>) data.get("balance");
                if (!balances.isEmpty()) {
                    // find InterimAvailable
                    for (Map<String, Object> balance : balances) {
                        if ("InterimAvailable".equals(balance.get("type"))) {
                            Map<String, Object> amountMap = (Map<String, Object>) balance.get("amount");
                            String amount = (String) amountMap.get("amount");
                            account.setBalance(new BigDecimal(amount));
                            accountRepository.save(account);
                            log.info("Updated balance for account {}", account.getExternalAccountId());
                            return; // Exit after finding and updating
                        }
                    }
                }
            }
        }
    }

    private void syncTransactionsForAccount(Account account, String accessToken, String consentId) {
        log.debug("Attempting to sync transactions for account {}...", account.getExternalAccountId());
        String fromDateTime = LocalDateTime.now().minusYears(1).format(DateTimeFormatter.ISO_DATE_TIME);
        String toDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        int currentPage = 1;
        int totalPages = 1;
        int totalNewTransactionsCount = 0;

        do {
            final int pageToFetch = currentPage;
            log.debug("Fetching page {} of transactions...", pageToFetch);

            Map<String, Object> transactionResponse = bankApiClient.getTransactionsForAccount(account.getExternalAccountId(), accessToken, consentId, fromDateTime, toDateTime, pageToFetch).block();

            log.debug("Raw transaction response for page {}: {}", pageToFetch, transactionResponse);

            if (transactionResponse == null || !transactionResponse.containsKey("data")) {
                log.warn("Transaction response is null or does not contain 'data' key on page {}. Aborting sync for this account.", pageToFetch);
                break;
            }

            if (transactionResponse.containsKey("meta")) {
                Map<String, Object> meta = (Map<String, Object>) transactionResponse.get("meta");
                if (meta != null && meta.containsKey("totalPages")) {
                    totalPages = (Integer) meta.get("totalPages");
                }
            }

            Map<String, Object> data = (Map<String, Object>) transactionResponse.get("data");
            if (data == null || !data.containsKey("transaction")) {
                log.warn("'transaction' key not found in data or is null on page {}. Continuing to next page.", pageToFetch);
                currentPage++;
                continue;
            }

            List<Map<String, Object>> transactions = (List<Map<String, Object>>) data.get("transaction");
            log.info("Fetched {} transactions for account {} on page {}", transactions.size(), account.getExternalAccountId(), pageToFetch);

            for (Map<String, Object> txnMap : transactions) {
                String externalTransactionId = (String) txnMap.get("transactionId");

                if (transactionRepository.findByExternalTransactionId(externalTransactionId).isPresent()) {
                    continue;
                }

                Transaction transaction = new Transaction();
                transaction.setUserId(account.getUserId());
                transaction.setAccount(account);
                transaction.setExternalTransactionId(externalTransactionId);
                transaction.setBookingDateTime(ZonedDateTime.parse((String) txnMap.get("bookingDateTime")).toLocalDateTime());

                Map<String, String> amountMap = (Map<String, String>) txnMap.get("amount");
                transaction.setAmount(new BigDecimal(amountMap.get("amount")));
                transaction.setCurrency(amountMap.get("currency"));
                transaction.setCreditDebitIndicator((String) txnMap.get("creditDebitIndicator"));
                transaction.setTransactionInformation((String) txnMap.get("transactionInformation"));
                transaction.setStatus((String) txnMap.get("status"));
                transactionRepository.save(transaction);
                totalNewTransactionsCount++;
            }

            currentPage++;
        } while (currentPage <= totalPages);

        log.info("Saved a total of {} new transactions for account {}", totalNewTransactionsCount, account.getExternalAccountId());
    }
}


