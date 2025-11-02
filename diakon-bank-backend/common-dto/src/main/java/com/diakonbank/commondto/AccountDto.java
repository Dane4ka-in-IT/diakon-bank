package com.diakonbank.commondto;

import com.diakonbank.commondto.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Long id;
    private Long userId;
    private String externalAccountId;
    private String accountNumber;
    private String nickname;
    private String accountType;
    private String accountSubType;
    private String currency;
    private BigDecimal balance;
    private String status;
    private LocalDate openingDate;
    private Instant createdAt;

    public static AccountDto fromEntity(Account account) {
        return new AccountDto(
                account.getId(),
                account.getUserId(),
                account.getExternalAccountId(),
                account.getAccountNumber(),
                account.getNickname(),
                account.getAccountType(),
                account.getAccountSubType(),
                account.getCurrency(),
                account.getBalance(),
                account.getStatus(),
                account.getOpeningDate(),
                account.getCreatedAt()
        );
    }
} 


