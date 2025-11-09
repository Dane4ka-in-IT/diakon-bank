package com.diakonbank.financialpulse.service.dto.ai;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiakonHelpLeaksRequestDto {
    @JsonProperty("transactions_json")
    private String transactionsJson;
}