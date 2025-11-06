package com.diakonbank.financialpulse.service.service;

import com.diakonbank.commondto.AccountDto;
import com.diakonbank.commondto.TransactionDto;
import com.diakonbank.financialpulse.service.mapper.AccountMapper;
import com.diakonbank.financialpulse.service.mapper.TransactionMapper;
import com.diakonbank.financialpulse.service.repository.AccountRepository;
import com.diakonbank.financialpulse.service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PulseQueryService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

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
}
