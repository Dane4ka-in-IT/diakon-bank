package com.diakonbank.bankapiservice.controller;

import com.diakonbank.commondto.AccountDto;
import com.diakonbank.commondto.TransactionDto;
import com.diakonbank.bankapiservice.exception.BankIntegrationException;
import com.diakonbank.bankapiservice.repository.AccountRepository;
import com.diakonbank.bankapiservice.repository.TransactionRepository;
import com.diakonbank.bankapiservice.service.BankIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BankApiController {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BankIntegrationService bankIntegrationService;

    // Hardcoded user for testing purposes
    private Long getCurrentUserId() {
        return 1L;
    }

    /**
     * Возвращает список счетов для текущего пользователя.
     */
    @GetMapping("/accounts")
    public List<AccountDto> getAccounts() {
        return accountRepository.findByUserId(getCurrentUserId()).stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список транзакций для текущего пользователя с возможностью фильтрации.
     */
    @GetMapping("/transactions")
    public List<TransactionDto> getTransactions(@RequestParam Optional<Long> accountId,
                                                @RequestParam Optional<LocalDate> fromDate,
                                                @RequestParam Optional<LocalDate> toDate) {
        // Basic implementation without filtering for now
        return transactionRepository.findByUserId(getCurrentUserId()).stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Запускает синхронизацию данных с банком для указанного пользователя.
     */
    @PostMapping("/internal/sync/{userId}")
    public void sync(@PathVariable Long userId) {
        bankIntegrationService.syncUserData(userId);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ProblemDetail> handleHttpClientErrorException(HttpClientErrorException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(problemDetail);
    }

    @ExceptionHandler(BankIntegrationException.class)
    public ResponseEntity<ProblemDetail> handleBankIntegrationException(BankIntegrationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
} 


