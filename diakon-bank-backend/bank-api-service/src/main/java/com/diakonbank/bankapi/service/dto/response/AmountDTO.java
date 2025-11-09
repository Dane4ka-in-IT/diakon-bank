package com.diakonbank.bankapi.service.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class AmountDTO {
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("currency")
    private String currency;
}