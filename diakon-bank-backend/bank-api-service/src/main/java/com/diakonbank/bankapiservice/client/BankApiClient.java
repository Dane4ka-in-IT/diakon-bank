package com.diakonbank.bankapiservice.client;

import com.diakonbank.bankapiservice.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import jakarta.annotation.PostConstruct;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class BankApiClient {

    @Value("${bank.client.id}")
    private String clientId;

    @Value("${bank.client.secret}")
    private String clientSecret;

    @Value("${bank.client.suffix}")
    private String clientSuffix;

    private final WebClient.Builder webClientBuilder;
    private WebClient bankClient;

    public BankApiClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    private void init() {
        this.bankClient = webClientBuilder
                .baseUrl("https://vbank.open.bankingapi.ru")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private String getSuffixedClientId() {
        return clientId + "-" + clientSuffix;
    }

    public Mono<String> getAccessToken() {
        return bankClient.post()
                .uri(uriBuilder -> uriBuilder.path("/auth/bank-token")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .build())
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .map(AccessTokenResponse::getAccessToken)
                .doOnError(this::logApiError)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }

    public Mono<String> getConsent(String accessToken) {
        Map<String, Object> body = Map.of(
                "client_id", getSuffixedClientId(),
                "permissions", List.of("ReadAccountsDetail", "ReadBalances", "ReadTransactionsDetail"),
                "reason", "Data aggregation for Diakon app",
                "requesting_bank", clientId,
                "requesting_bank_name", "Diakon App"
        );
        return bankClient.post()
                .uri("/account-consents/request")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("X-Requesting-Bank", clientId)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(ConsentResponse.class)
                .map(ConsentResponse::getConsentId)
                .doOnError(this::logApiError)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }

    public Mono<ConsentDetailsResponse> getConsentDetails(String consentId, String accessToken) {
        return this.bankClient.get()
                .uri("/account-consents/{consentId}", consentId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(ConsentDetailsResponse.class)
                .doOnError(this::logApiError);
    }

    public Mono<List<BankAcountDTO>> getAccounts(String accessToken, String consentId) {
        return bankClient.get()
                .uri(uriBuilder -> uriBuilder.path("/accounts")
                        .queryParam("client_id", getSuffixedClientId())
                        .build())
                .headers(h -> {
                    h.setBearerAuth(accessToken);
                    h.set("X-Consent-Id", consentId);
                    h.set("X-Requesting-Bank", clientId);
                })
                .retrieve()
                .bodyToMono(AccountListResponse.class)
                .map(response -> Optional.ofNullable(response.getData())
                        .map(AccountListResponse.AccountData::getAccount)
                        .orElse(Collections.emptyList()))
                .doOnError(this::logApiError)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }

    public Mono<BankBalanceResponseDTO> getAccountBalances(String externalAccountId, String accessToken, String consentId) {
        return bankClient.get()
                .uri("/accounts/{accountId}/balances", externalAccountId)
                .headers(h -> {
                    h.setBearerAuth(accessToken);
                    h.set("X-Consent-Id", consentId);
                    h.set("X-Requesting-Bank", clientId);
                })
                .retrieve()
                .bodyToMono(BankBalanceResponseDTO.class)
                .doOnError(this::logApiError)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }

    public Mono<BankTransactionResponseDTO> getTransactionsForAccount(String externalAccountId, String accessToken, String consentId, String fromDateTime, String toDateTime, int pageToFetch) {
        return bankClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{accountId}/transactions")
                        .queryParam("fromBookingDateTime", fromDateTime)
                        .queryParam("toBookingDateTime", toDateTime)
                        .queryParam("page", pageToFetch)
                        .build(externalAccountId))
                .headers(h -> {
                    h.setBearerAuth(accessToken);
                    h.set("X-Consent-Id", consentId);
                    h.set("X-Requesting-Bank", clientId);
                })
                .retrieve()
                .bodyToMono(BankTransactionResponseDTO.class)
                .doOnError(this::logApiError)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }

    private void logApiError(Throwable e) {
        if (e instanceof WebClientResponseException ex) {
            log.error("API Error: {} \nResponse Body: {}", ex.getMessage(), ex.getResponseBodyAsString());
        } else {
            log.error("API Error: {}", e.getMessage());
        }
    }
}
