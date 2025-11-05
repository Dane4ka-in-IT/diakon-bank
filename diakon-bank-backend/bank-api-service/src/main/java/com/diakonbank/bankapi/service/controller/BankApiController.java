package com.diakonbank.bankapi.service.controller;

import com.diakonbank.commondto.AccountDto;
import com.diakonbank.commondto.TransactionDto;
import com.diakonbank.bankapi.service.exception.BankIntegrationException;
import com.diakonbank.bankapi.service.repository.AccountRepository;
import com.diakonbank.bankapi.service.repository.TransactionRepository;
import com.diakonbank.bankapi.service.service.BankIntegrationService;
import com.diakonbank.bankapi.service.mapper.AccountMapper;
import com.diakonbank.bankapi.service.mapper.TransactionMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bank")
@RequiredArgsConstructor
@Slf4j
public class BankApiController {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BankIntegrationService bankIntegrationService;

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getAccounts() {
        Long currentUserId = getCurrentUserId();
        List<AccountDto> accountDtos = accountRepository.findByOwnerUserId(currentUserId).stream()
                .map(AccountMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accountDtos);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactions() {

        Long currentUserId = getCurrentUserId();
        List<TransactionDto> transactionDtos = transactionRepository.findByOwnerUserId(currentUserId).stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    @PostMapping("/internal/sync/{userId}")
    public ResponseEntity<Void> sync(@PathVariable Long userId) {
        log.info("Starting manual data synchronization for user ID: {}", userId);
        bankIntegrationService.syncUserData(userId);
        log.info("Manual data synchronization for user ID: {} completed.", userId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ProblemDetail> handleHttpClientErrorException(HttpClientErrorException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getResponseBodyAsString());
        problemDetail.setTitle("Ошибка при обращении к внешнему банковскому API");
        return ResponseEntity.status(ex.getStatusCode()).body(problemDetail);
    }

    @ExceptionHandler(BankIntegrationException.class)
    public ResponseEntity<ProblemDetail> handleBankIntegrationException(BankIntegrationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("Внутренняя ошибка интеграции с банком");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}
