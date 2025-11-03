package com.diakonbank.bankapiservice.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IdentificationDTO {
    @JsonProperty("SchemeName") private String schemeName;
    @JsonProperty("Identification") private String identification;
    @JsonProperty("Name") private String name;
    @JsonProperty("SecondaryIdentification") private String secondaryIdentification;
}
