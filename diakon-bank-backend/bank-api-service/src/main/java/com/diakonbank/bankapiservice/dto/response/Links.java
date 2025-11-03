package com.diakonbank.bankapiservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Links {
    @JsonProperty("self")
    private String self;
}
