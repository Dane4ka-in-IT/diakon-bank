package com.diakonbank.financialpulse.service.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
@Entity
@Table(name = "accounts")
@Data
public class Account {
    @Id
    private Long id;
    private Long ownerUserId;
    private String bankName;
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
    private Instant updatedAt;
}