package com.diakonbank.commondto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiakonHelpResponseDto {

    @JsonProperty("ai_response")
    private String aiResponse;
}
