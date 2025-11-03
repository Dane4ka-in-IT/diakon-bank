package com.diakonbank.bankapiservice.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class BankBalanceDTO {
    @JsonProperty("Balance") private List<BalanceItem> balance;
    @Data
    public static class BalanceItem {
        @JsonProperty("AccountId") private String accountId;
        @JsonProperty("Amount") private AmountDTO amount;
        @JsonProperty("CreditDebitIndicator") private String creditDebitIndicator;
        @JsonProperty("Type") private String type;
    }
}
