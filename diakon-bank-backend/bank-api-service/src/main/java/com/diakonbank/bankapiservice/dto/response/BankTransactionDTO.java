package com.diakonbank.bankapiservice.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class BankTransactionDTO {
    @JsonProperty("AccountId") private String accountId;
    @JsonProperty("TransactionId") private String transactionId;
    @JsonProperty("Amount") private AmountDTO amount;
    @JsonProperty("CreditDebitIndicator") private String creditDebitIndicator;
    @JsonProperty("Status") private String status;
    @JsonProperty("BookingDateTime") private String bookingDateTime;
    @JsonProperty("ValueDateTime") private String valueDateTime;
    @JsonProperty("TransactionInformation") private String transactionInformation;
}
