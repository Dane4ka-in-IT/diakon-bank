package com.diakonbank.commondto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChartUrlResponseDto {
    private String url;
    private String token;
    private long expirySeconds;
}
