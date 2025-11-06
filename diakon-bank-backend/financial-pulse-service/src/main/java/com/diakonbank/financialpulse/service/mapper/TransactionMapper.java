package com.diakonbank.financialpulse.service.mapper;

import com.diakonbank.commondto.TransactionDto;
import com.diakonbank.financialpulse.service.entity.Transaction;

public final class TransactionMapper {
    private TransactionMapper() {}

    public static TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .accountId(entity.getAccountId())
                .ownerUserId(entity.getOwnerUserId())
                .externalTransactionId(entity.getExternalTransactionId())
                .bookingDateTime(entity.getBookingDateTime())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .creditDebitIndicator(entity.getCreditDebitIndicator())
                .transactionInformation(entity.getTransactionInformation())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
