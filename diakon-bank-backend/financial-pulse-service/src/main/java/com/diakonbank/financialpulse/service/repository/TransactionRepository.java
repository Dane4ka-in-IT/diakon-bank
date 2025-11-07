package com.diakonbank.financialpulse.service.repository;

import com.diakonbank.financialpulse.service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByOwnerUserId(Long ownerUserId);

    List<Transaction> findByOwnerUserIdAndBookingDateTimeBetween(Long ownerUserId, LocalDateTime start, LocalDateTime end);

    List<Transaction> findTop15ByOwnerUserIdOrderByBookingDateTimeDesc(Long ownerUserId);

    List<Transaction> findByOwnerUserIdAndBookingDateTimeAfter(Long ownerUserId, LocalDateTime startDate);

}
