package com.diakonbank.bankapi.service.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class IdentificationDTO {
    @JsonProperty("schemeName")
    private String schemeName;
    @JsonProperty("identification")
    private String identification;
    @JsonProperty("name")
    private String name;
    @JsonProperty("secondaryIdentification")
    private String secondaryIdentification;
}