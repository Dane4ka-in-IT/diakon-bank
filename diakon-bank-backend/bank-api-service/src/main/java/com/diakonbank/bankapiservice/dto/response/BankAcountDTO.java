package com.diakonbank.bankapiservice.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
@Data
public class BankAcountDTO {
    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("accountType")
    private String accountType;

    @JsonProperty("accountSubType")
    private String accountSubType;

    @JsonProperty("description")
    private String description;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("openingDate")
    private String openingDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("account")
    private List<IdentificationDTO> account;
}
