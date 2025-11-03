package com.diakonbank.bankapiservice.repository;

import com.diakonbank.bankapiservice.entity.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserId(Long userId);
    Optional<Transaction> findByExternalTransactionId(String externalTransactionId);
} 


