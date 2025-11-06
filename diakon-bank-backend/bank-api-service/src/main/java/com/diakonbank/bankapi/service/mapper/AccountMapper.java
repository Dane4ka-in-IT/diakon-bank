package com.diakonbank.bankapi.service.mapper;

import com.diakonbank.bankapi.service.entity.Account;
import com.diakonbank.commondto.AccountDto;

public final class AccountMapper {

    private AccountMapper() {}

    public static AccountDto toDto(Account entity) {
        if (entity == null) {
            return null;
        }

        return AccountDto.builder()
                .id(entity.getId())
                .ownerUserId(entity.getOwnerUserId())
                .bankName(entity.getBankName())
                .externalAccountId(entity.getExternalAccountId())
                .accountNumber(entity.getAccountNumber())
                .nickname(entity.getNickname())
                .accountType(entity.getAccountType())
                .accountSubType(entity.getAccountSubType())
                .currency(entity.getCurrency())
                .balance(entity.getBalance())
                .status(entity.getStatus())
                .openingDate(entity.getOpeningDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
