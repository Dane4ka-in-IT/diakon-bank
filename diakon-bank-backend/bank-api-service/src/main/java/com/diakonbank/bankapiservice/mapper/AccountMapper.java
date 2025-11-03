package com.diakonbank.bankapiservice.mapper;

import com.diakonbank.bankapiservice.entity.Account;
import com.diakonbank.commondto.AccountDto;

public class AccountMapper {

    public static AccountDto toDto(Account entity) {
        if (entity == null) {
            return null;
        }

        return AccountDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
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
                .build();
    }
}
