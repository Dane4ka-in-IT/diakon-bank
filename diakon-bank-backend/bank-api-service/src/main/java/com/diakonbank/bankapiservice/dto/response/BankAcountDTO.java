package com.diakonbank.bankapiservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BankAcountDTO {

    @JsonProperty("AccountId")
    private String accountId;

    @JsonProperty("Currency")
    private String currency;

    @JsonProperty("AccountType")
    private String accountType;

    @JsonProperty("AccountSubType")
    private String accountSubType;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Nickname")
    private String nickname;

    @JsonProperty("OpeningDate")
    private String openingDate;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Account")
    private List<IdentificationDTO> account;
}
