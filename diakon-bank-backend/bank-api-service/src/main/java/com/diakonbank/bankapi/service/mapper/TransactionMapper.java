package com.diakonbank.bankapi.service.mapper;
import com.diakonbank.bankapi.service.entity.Transaction;
import com.diakonbank.commondto.TransactionDto;
public final class TransactionMapper {
    private TransactionMapper() {}
    public static TransactionDto toDto(Transaction entity) {
        if (entity == null) {
            return null;
        }
        return TransactionDto.builder()
                .id(entity.getId())
                .accountId(entity.getAccount() != null ? entity.getAccount().getId() : null)
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