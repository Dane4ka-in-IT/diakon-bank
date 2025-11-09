package com.diakonbank.bankapi.service.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
@Data
public class DataDTO {
    @JsonProperty("transaction") private List<BankTransactionDTO> transaction;
}