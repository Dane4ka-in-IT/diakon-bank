package com.diakonbank.bankapi.service.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
public class AccountListResponse {
    @JsonProperty("data")
    private AccountData data;
    @Getter
    @Setter
    public static class AccountData {
        @JsonProperty("account")
        private List<BankAcountDTO> account;
    }
}