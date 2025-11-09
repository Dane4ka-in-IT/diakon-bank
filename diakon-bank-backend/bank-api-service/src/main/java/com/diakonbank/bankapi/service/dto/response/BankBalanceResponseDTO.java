package com.diakonbank.bankapi.service.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class BankBalanceResponseDTO {
    @JsonProperty("data")
    private BankBalanceDTO data;
    @JsonProperty("links")
    private Links links;
    @JsonProperty("meta")
    private MetaDTO meta;
}