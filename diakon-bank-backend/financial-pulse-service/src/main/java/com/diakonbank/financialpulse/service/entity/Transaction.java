package com.diakonbank.financialpulse.service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    private Long id;
    private Long accountId;
    private Long ownerUserId;
    private String externalTransactionId;
    private LocalDateTime bookingDateTime;
    private BigDecimal amount;
    private String currency;
    private String creditDebitIndicator;
    private String transactionInformation;
    private String status;
    private Instant createdAt;
}
