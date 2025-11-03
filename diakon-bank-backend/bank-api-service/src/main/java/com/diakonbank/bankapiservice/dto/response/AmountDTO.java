package com.diakonbank.bankapiservice.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AmountDTO {
    @JsonProperty("Amount") private String amount;
    @JsonProperty("Currency") private String currency;
}
