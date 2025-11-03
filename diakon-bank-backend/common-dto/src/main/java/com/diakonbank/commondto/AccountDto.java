package com.diakonbank.commondto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
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
} 


