package com.diakonbank.bankapi.service.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class BankTransactionResponseDTO {
    @JsonProperty("data")
    private DataDTO data;

    @JsonProperty("links")
    private Links links;

    @JsonProperty("meta")
    private MetaDTO meta;
}
