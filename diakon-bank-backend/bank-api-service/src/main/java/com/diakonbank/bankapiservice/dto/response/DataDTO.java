package com.diakonbank.bankapiservice.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class DataDTO {
    @JsonProperty("Transaction") private List<BankTransactionDTO> transaction;
}
