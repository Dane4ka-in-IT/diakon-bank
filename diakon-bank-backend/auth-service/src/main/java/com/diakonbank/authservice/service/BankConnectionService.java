package com.diakonbank.authservice.service;
import com.diakonbank.authservice.client.BankApiInternalClient;
import com.diakonbank.authservice.dto.request.ConnectBankRequest;
import com.diakonbank.authservice.dto.request.SyncBankRequest;
import com.diakonbank.authservice.entity.BankConnection;
import com.diakonbank.authservice.entity.User;
import com.diakonbank.authservice.repository.BankConnectionRepository;
import com.diakonbank.authservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;
import java.time.Instant;
@Service
@Slf4j
public class BankConnectionService {
    private final BankConnectionRepository bankConnectionRepository;
    private final UserRepository userRepository;
    private final BankApiInternalClient bankApiInternalClient;
    private final EncryptionService encryptionService;
    public BankConnectionService(BankConnectionRepository bankConnectionRepository, UserRepository userRepository,
                                 BankApiInternalClient bankApiInternalClient, EncryptionService encryptionService) {
        this.bankConnectionRepository = bankConnectionRepository;
        this.userRepository = userRepository;
        this.bankApiInternalClient = bankApiInternalClient;
        this.encryptionService = encryptionService;
    }
    public void connectBank(ConnectBankRequest connectBankRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        log.info("Starting bank connection process for user: {}", username);
        String bankName = connectBankRequest.getBank().name();
        bankApiInternalClient.validateCredentials(connectBankRequest.getBankLogin(), connectBankRequest.getBankPassword(), bankName)
                .filter(isValid -> isValid)
                .doOnSuccess(isValid -> {
                    if (isValid) {
                        log.info("Bank credentials validated successfully for user: {}", username);
                        BankConnection bankConnection = new BankConnection();
                        bankConnection.setUser(user);
                        bankConnection.setBankLogin(connectBankRequest.getBankLogin());
                        bankConnection.setEncryptedBankPassword(encryptionService.encrypt(connectBankRequest.getBankPassword()));
                        bankConnection.setBankIdentifier(connectBankRequest.getBank().name().toLowerCase());
                        bankConnection.setStatus("ACTIVE");
                        bankConnection.setCreatedAt(Instant.now());
                        bankConnection.setUpdatedAt(Instant.now());
                        bankConnectionRepository.save(bankConnection);
                        log.info("Saved new bank connection for user: {}", username);
                        log.info("Triggering user data synchronization for user: {}", username);
                        bankApiInternalClient.syncUser(connectBankRequest.getBankLogin(), connectBankRequest.getBankPassword(), user.getId(), bankName)
                                .subscribeOn(Schedulers.boundedElastic())
                                .subscribe(
                                        null,
                                        error -> log.error("Error during user sync for user: {}", username, error),
                                        () -> log.info("Successfully initiated user sync for user: {}", username)
                                );
                    } else {
                        log.warn("Bank credential validation failed for user: {}", username);
                    }
                }).subscribe(
                        null,
                        error -> log.error("Error during bank credential validation for user: {}", username, error)
                );
    }
    public void syncBank(SyncBankRequest syncBankRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        log.info("Starting bank sync process for user: {}", username);
        String bankIdentifier = syncBankRequest.getBank().name().toLowerCase();
        BankConnection bankConnection = bankConnectionRepository.findByUserAndBankIdentifier(user, bankIdentifier)
                .orElseThrow(() -> new RuntimeException("Bank connection not found for user " + username + " and bank " + bankIdentifier));
        log.info("Found bank connection for user: {}", username);
        String decryptedPassword = encryptionService.decrypt(bankConnection.getEncryptedBankPassword());
        log.info("Triggering user data synchronization for user: {}", username);
        bankApiInternalClient.syncUser(bankConnection.getBankLogin(), decryptedPassword, user.getId(), syncBankRequest.getBank().name())
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                    null,
                    error -> log.error("Error during bank sync for user: {}", username, error),
                    () -> log.info("Successfully initiated bank sync for user: {}", username)
                );
    }
}