package com.diakonbank.bankapiservice.client;

import com.diakonbank.bankapiservice.exception.BankIntegrationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import jakarta.annotation.PostConstruct;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class BankApiClient {

    @Value("${bank.client.id}")
    private String clientId;

    @Value("${bank.client.secret}")
    private String clientSecret;

    @Value("${bank.client.suffix}")
    private String clientSuffix;

    private final WebClient.Builder bankClientBuilder;
    private WebClient bankClient;

    public BankApiClient(WebClient.Builder bankClientBuilder) {
        this.bankClientBuilder = bankClientBuilder;
    }

    @PostConstruct
    private void init() {
        this.bankClient = bankClientBuilder
                .baseUrl("https://vbank.open.bankingapi.ru")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private String getSuffixedClientId() {
        return clientId + "-" + clientSuffix;
    }

    public Mono<String> getAccessToken() {
        log.debug("Attempting to get access token...");
        return bankClient.post()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/auth/bank-token")
                            .queryParam("client_id", clientId)
                            .queryParam("client_secret", clientSecret);
                    log.debug("Requesting URI for access token: {}", builder.build());
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(response -> log.debug("Raw access token response: {}", response))
                .map(response -> (String) response.get("access_token"))
                .doOnError(this::logApiError)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }

    public Mono<String> getConsent(String accessToken) {
        log.debug("Attempting to get consent with accessToken: {}", accessToken);
        Map<String, Object> body = Map.of(
                "client_id", getSuffixedClientId(),
                "permissions", List.of("ReadAccountsDetail", "ReadBalances", "ReadTransactionsDetail"),
                "reason", "Data aggregation for Diakon app",
                "requesting_bank", clientId,
                "requesting_bank_name", "Diakon App"
        );
        log.debug("Consent request body: {}", body);

        return bankClient.post()
                .uri("/account-consents/request")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("X-Requesting-Bank", clientId)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(response -> log.debug("Raw consent response: {}", response))
                .map(response -> (String) response.get("consent_id"))
                .doOnError(this::logApiError)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }

    public Mono<List<Map<String, Object>>> getAccounts(String accessToken, String consentId) {
        log.debug("Attempting to get accounts with accessToken: {}, consentId: {}", accessToken, consentId);
        return bankClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/accounts")
                            .queryParam("client_id", getSuffixedClientId());
                    log.debug("Requesting URI: {}", builder.build().toString());
                    return builder.build();
                })
                .headers(h -> {
                    h.setBearerAuth(accessToken);
                    h.set("X-Consent-Id", consentId);
                    h.set("X-Requesting-Bank", clientId);
                    log.debug("Request Headers: {}", h);
                })
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(response -> log.debug("Raw accounts response: {}", response))
                .map(response -> {
                    if (response == null || !response.containsKey("data")) {
                        log.warn("Response is null or does not contain 'data' key. Response: {}", response);
                        throw new BankIntegrationException("Invalid response structure from bank API: 'data' key is missing.");
                    }
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    if (data == null || !data.containsKey("account")) {
                        log.warn("'account' key not found in data or is null. Data: {}", data);
                        throw new BankIntegrationException("No accounts found for the user.");
                    }

                    List<Map<String, Object>> accounts = (List<Map<String, Object>>) data.get("account");

                    if (accounts == null || accounts.isEmpty()) {
                        log.warn("'accounts' list is null or empty. Data: {}", data);
                        throw new BankIntegrationException("No accounts found for the user.");
                    }
                    return accounts;
                })
                .doOnError(this::logApiError)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }

    public Mono<Map<String, Object>> getAccountBalances(String externalAccountId, String accessToken, String consentId) {
        return bankClient.get()
                .uri("/accounts/{accountId}/balances", externalAccountId)
                .headers(h -> {
                    h.setBearerAuth(accessToken);
                    h.set("X-Consent-Id", consentId);
                    h.set("X-Requesting-Bank", clientId);
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnError(this::logApiError)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }

    public Mono<Map<String, Object>> getTransactionsForAccount(String externalAccountId, String accessToken, String consentId, String fromDateTime, String toDateTime, int pageToFetch) {
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
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnError(this::logApiError)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }

    private void logApiError(Throwable e) {
        if (e instanceof WebClientResponseException) {
            WebClientResponseException ex = (WebClientResponseException) e;
            log.error("API Error: {} \nResponse Body: {}", ex.getMessage(), ex.getResponseBodyAsString());
        } else {
            log.error("API Error: {}", e.getMessage());
        }
    }
} 


