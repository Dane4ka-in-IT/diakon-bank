package com.diakonbank.bankapi.service.client;
import com.diakonbank.bankapi.service.dto.response.*;
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
    private final WebClient.Builder webClientBuilder;
    public BankApiClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    private String getClientId(String login) {
        int lastDash = login.lastIndexOf('-');
        if (lastDash != -1) {
            return login.substring(0, lastDash);
        }
        return login;
    }
    private WebClient getClientForBank(String bank) {
        String baseUrl = String.format("https://%s.open.bankingapi.ru", bank.toLowerCase());
        return webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    public Mono<String> getAccessToken(String login, String password, String bank) {
        String clientId = getClientId(login);
        log.info("Requesting access token for clientId: {}", clientId);
        return getClientForBank(bank).post()
                .uri(uriBuilder -> uriBuilder.path("/auth/bank-token")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", password)
                        .build())
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .map(AccessTokenResponse::getAccessToken)
                .doOnError(this::logApiError)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }
    public Mono<String> getConsent(String accessToken, String login, String bank) {
        String clientId = getClientId(login);
        Map<String, Object> body = Map.of(
                "client_id", login,
                "permissions", List.of("ReadAccountsDetail", "ReadBalances", "ReadTransactionsDetail"),
                "reason", "Data aggregation for Diakon app",
                "requesting_bank", clientId,
                "requesting_bank_name", "Diakon App"
        );
        return getClientForBank(bank).post()
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
    public Mono<ConsentDetailsResponse> getConsentDetails(String consentId, String accessToken, String bank) {
        return getClientForBank(bank).get()
                .uri("/account-consents/{consentId}", consentId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(ConsentDetailsResponse.class)
                .doOnError(this::logApiError);
    }
    public Mono<List<BankAcountDTO>> getAccounts(String accessToken, String consentId, String login, String bank) {
        String clientId = getClientId(login);
        return getClientForBank(bank).get()
                .uri(uriBuilder -> uriBuilder.path("/accounts")
                        .queryParam("client_id", login)
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
    public Mono<BankBalanceResponseDTO> getAccountBalances(String externalAccountId, String accessToken, String consentId, String login, String bank) {
        String clientId = getClientId(login);
        return getClientForBank(bank).get()
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
    public Mono<BankTransactionResponseDTO> getTransactionsForAccount(String externalAccountId, String accessToken, String consentId, String fromDateTime, String toDateTime, int pageToFetch, String login, String bank) {
        String clientId = getClientId(login);
        return getClientForBank(bank).get()
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