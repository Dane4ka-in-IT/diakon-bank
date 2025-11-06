package com.diakonbank.bankapi.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ConsentDetailsResponse {

    @JsonProperty("data")
    private ConsentData data;

    @JsonProperty("links")
    private Links links;

    @JsonProperty("meta")
    private Meta meta;

    @Data
    public static class ConsentData {
        @JsonProperty("consentId")
        private String consentId;

        @JsonProperty("status")
        private String status;

        @JsonProperty("creationDateTime")
        private String creationDateTime;

        @JsonProperty("statusUpdateDateTime")
        private String statusUpdateDateTime;

        @JsonProperty("permissions")
        private List<String> permissions;

        @JsonProperty("expirationDateTime")
        private String expirationDateTime;
    }

    @Data
    public static class Links {
        @JsonProperty("self")
        private String self;
    }

    @Data
    public static class Meta {
    }
}
