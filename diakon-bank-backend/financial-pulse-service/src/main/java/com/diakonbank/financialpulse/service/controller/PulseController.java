package com.diakonbank.financialpulse.service.controller;

import com.diakonbank.commondto.AccountDto;
import com.diakonbank.commondto.TransactionDto;
import com.diakonbank.financialpulse.service.service.PulseQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pulse")
@RequiredArgsConstructor
public class PulseController {

    private final PulseQueryService pulseQueryService;

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getAccounts(@RequestHeader("X-User-Id") Long userId) {
        List<AccountDto> accounts = pulseQueryService.getAccountsByOwnerId(userId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactions(@RequestHeader("X-User-Id") Long userId) {
        List<TransactionDto> transactions = pulseQueryService.getTransactionsByOwnerId(userId);
        return ResponseEntity.ok(transactions);
    }
}
