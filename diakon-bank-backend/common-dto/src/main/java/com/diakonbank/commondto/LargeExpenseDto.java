package com.diakonbank.commondto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LargeExpenseDto {
    @JsonProperty("name")
    private String name;

    @JsonProperty("amount")
    private BigDecimal amount;
}
