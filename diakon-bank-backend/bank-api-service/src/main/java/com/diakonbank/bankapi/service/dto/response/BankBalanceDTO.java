package com.diakonbank.bankapi.service.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
@Data
public class BankBalanceDTO {
    @JsonProperty("balance") private List<BalanceItem> balance;
    @Data
    public static class BalanceItem {
        @JsonProperty("accountId")
        private String accountId;
        @JsonProperty("amount")
        private AmountDTO amount;
        @JsonProperty("creditDebitIndicator")
        private String creditDebitIndicator;
        @JsonProperty("type")
        private String type;
    }
}