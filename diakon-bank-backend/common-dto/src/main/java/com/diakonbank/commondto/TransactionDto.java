package com.diakonbank.commondto;

import com.diakonbank.commondto.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private Long id;
    private Long userId;
    private Long accountId;
    private String externalTransactionId;
    private LocalDateTime bookingDateTime;
    private BigDecimal amount;
    private String currency;
    private String creditDebitIndicator;
    private String transactionInformation;
    private String status;
    private Instant createdAt;

    public static TransactionDto fromEntity(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getAccount().getId(),
                transaction.getExternalTransactionId(),
                transaction.getBookingDateTime(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getCreditDebitIndicator(),
                transaction.getTransactionInformation(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );
    }
} 


