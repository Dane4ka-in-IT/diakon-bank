package com.diakonbank.bankapiservice.entity;

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

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private String externalTransactionId;

    private LocalDateTime bookingDateTime;

    private BigDecimal amount;

    private String currency;

    private String creditDebitIndicator;

    private String transactionInformation;

    private String status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}


