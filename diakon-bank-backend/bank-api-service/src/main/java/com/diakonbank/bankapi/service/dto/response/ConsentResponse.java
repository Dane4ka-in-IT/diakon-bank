package com.diakonbank.bankapi.service.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConsentResponse {
    @JsonProperty("consent_id") private String consentId;
    private String status;
}