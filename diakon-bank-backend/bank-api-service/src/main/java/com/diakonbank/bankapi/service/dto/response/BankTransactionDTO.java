package com.diakonbank.bankapi.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BankTransactionDTO {

    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("amount")
    private AmountDTO amount;

    @JsonProperty("creditDebitIndicator")
    private String creditDebitIndicator;

    @JsonProperty("status")
    private String status;

    @JsonProperty("bookingDateTime")
    private String bookingDateTime;

    @JsonProperty("valueDateTime")
    private String valueDateTime;

    @JsonProperty("transactionInformation")
    private String transactionInformation;

    @JsonProperty("bankTransactionCode")
    private BankTransactionCodeDTO bankTransactionCode;

    @Data
    public static class BankTransactionCodeDTO {
        @JsonProperty("code")
        private String code;
    }
}
