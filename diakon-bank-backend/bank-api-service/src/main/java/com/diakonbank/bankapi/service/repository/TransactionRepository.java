package com.diakonbank.bankapi.service.repository;

import com.diakonbank.bankapi.service.entity.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByOwnerUserId(Long ownerUserId);
    Optional<Transaction> findByExternalTransactionId(String externalTransactionId);
} 


