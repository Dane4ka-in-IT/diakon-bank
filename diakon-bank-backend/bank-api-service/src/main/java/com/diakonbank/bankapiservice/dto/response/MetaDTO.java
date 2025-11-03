package com.diakonbank.bankapiservice.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MetaDTO {
    @JsonProperty("TotalPages") private Integer totalPages;
}
