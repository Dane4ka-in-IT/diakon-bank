package com.diakonbank.commondto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private Long id;
    private Long accountId;

    private String externalTransactionId;
    private LocalDateTime bookingDateTime;
    private BigDecimal amount;
    private String currency;
    private String creditDebitIndicator;
    private String transactionInformation;
    private String status;

    private Instant createdAt;
}
