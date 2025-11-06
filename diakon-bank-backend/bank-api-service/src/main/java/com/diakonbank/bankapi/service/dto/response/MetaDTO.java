package com.diakonbank.bankapi.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MetaDTO {
    @JsonProperty("totalPages")
    private Integer totalPages;
}
