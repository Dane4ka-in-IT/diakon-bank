package com.diakonbank.financialpulse.service.dto.ai;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class DiakonHelpRequestDto {
    @JsonProperty("user_text")
    private String userText;
    @JsonProperty("transactions_json")
    private String transactionsJson;
}