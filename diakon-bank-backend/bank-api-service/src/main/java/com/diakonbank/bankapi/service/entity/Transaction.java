package com.diakonbank.bankapi.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(name = "external_transaction_id", unique = true, nullable = false)
    private String externalTransactionId;

    @Column(name = "booking_date_time")
    private LocalDateTime bookingDateTime;

    private BigDecimal amount;

    private String currency;

    @Column(name = "credit_debit_indicator")
    private String creditDebitIndicator;

    @Column(columnDefinition = "TEXT")
    private String transactionInformation;

    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}