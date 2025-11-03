package com.diakonbank.bankapiservice.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BankTransactionResponseDTO {
    @JsonProperty("Data") private DataDTO data;
    @JsonProperty("Meta") private MetaDTO meta;
}
