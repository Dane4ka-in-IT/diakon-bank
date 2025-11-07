package com.diakonbank.financialpulse.service.client;

import com.diakonbank.financialpulse.service.dto.ai.DiakonHelpRequestDto;
import com.diakonbank.financialpulse.service.dto.ai.DiakonHelpResponseDto;
import com.diakonbank.financialpulse.service.dto.ai.DiakonHelpLeaksRequestDto;
import com.diakonbank.commondto.LeaksResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiakonHelpClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${ai.gateway.url}")
    private String aiGatewayUrl;

    public Mono<DiakonHelpResponseDto> getAnalysis(DiakonHelpRequestDto request) {
        WebClient client = webClientBuilder.build();
        return client.post()
                .uri(aiGatewayUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(DiakonHelpResponseDto.class);
    }

    public Mono<LeaksResponseDto> findLeaks(String transactionsJson) {
        WebClient client = webClientBuilder.build();
        String leaksUrl = aiGatewayUrl.replace("/chat", "/leaks");

        DiakonHelpLeaksRequestDto requestBodyDto = new DiakonHelpLeaksRequestDto(transactionsJson);

        return client.post()
                .uri(leaksUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(requestBodyDto))
                .retrieve()
                .bodyToMono(LeaksResponseDto.class);
    }
}
