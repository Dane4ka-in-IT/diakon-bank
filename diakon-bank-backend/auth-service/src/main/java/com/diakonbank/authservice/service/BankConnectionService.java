package com.diakonbank.authservice.service;

import com.diakonbank.authservice.client.BankApiInternalClient;
import com.diakonbank.authservice.dto.request.ConnectBankRequest;
import com.diakonbank.authservice.entity.BankConnection;
import com.diakonbank.authservice.entity.User;
import com.diakonbank.authservice.repository.BankConnectionRepository;
import com.diakonbank.authservice.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;

@Service
public class BankConnectionService {

    private final BankConnectionRepository bankConnectionRepository;
    private final UserRepository userRepository;
    private final BankApiInternalClient bankApiInternalClient;
    private final PasswordEncoder passwordEncoder;

    public BankConnectionService(BankConnectionRepository bankConnectionRepository, UserRepository userRepository,
                                 BankApiInternalClient bankApiInternalClient, PasswordEncoder passwordEncoder) {
        this.bankConnectionRepository = bankConnectionRepository;
        this.userRepository = userRepository;
        this.bankApiInternalClient = bankApiInternalClient;
        this.passwordEncoder = passwordEncoder;
    }

    public void connectBank(ConnectBankRequest connectBankRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String bankName = connectBankRequest.getBank().name();
        bankApiInternalClient.validateCredentials(connectBankRequest.getBankLogin(), connectBankRequest.getBankPassword(), bankName)
                .filter(isValid -> isValid)
                .doOnSuccess(isValid -> {
                    if (isValid) {
                        BankConnection bankConnection = new BankConnection();
                        bankConnection.setUser(user);
                        bankConnection.setBankLogin(connectBankRequest.getBankLogin());
                        bankConnection.setEncryptedBankPassword(passwordEncoder.encode(connectBankRequest.getBankPassword()));
                        bankConnection.setBankIdentifier(connectBankRequest.getBank().name().toLowerCase());
                        bankConnection.setStatus("ACTIVE");
                        bankConnection.setCreatedAt(Instant.now());
                        bankConnection.setUpdatedAt(Instant.now());
                        bankConnectionRepository.save(bankConnection);

                        bankApiInternalClient.syncUser(connectBankRequest.getBankLogin(), connectBankRequest.getBankPassword(), user.getId(), bankName)
                                .subscribeOn(Schedulers.boundedElastic())
                                .subscribe();
                    }
                }).subscribe();
    }
} 