package com.diakonbank.bankapiservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class AccountListResponse {
    @JsonProperty("Data")
    private AccountData data;

    @Data
    public static class AccountData {
        @JsonProperty("Account")
        private List<BankAcountDTO> account;
    }
}