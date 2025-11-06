package com.diakonbank.authservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class BankApiInternalClient {

    private final WebClient bankApiClient;

    public BankApiInternalClient(WebClient.Builder webClientBuilder) {
        this.bankApiClient = webClientBuilder.baseUrl("http://localhost:6666/internal").build();
    }

    public Mono<Boolean> validateCredentials(String login, String password) {
        return bankApiClient.post()
                .uri("/validate-credentials")
                .bodyValue(new CredentialValidationRequest(login, password))
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Void> syncUser(String login, String password, Long ownerUserId) {
        return bankApiClient.post()
                .uri("/sync-user")
                .bodyValue(new UserSyncRequest(login, password, ownerUserId))
                .retrieve()
                .bodyToMono(Void.class);
    }

    // DTOs for requests
    private static class CredentialValidationRequest {
        public String login;
        public String password;

        public CredentialValidationRequest(String login, String password) {
            this.login = login;
            this.password = password;
        }
    }

    private static class UserSyncRequest {
        public String login;
        public String password;
        public Long ownerUserId;

        public UserSyncRequest(String login, String password, Long ownerUserId) {
            this.login = login;
            this.password = password;
            this.ownerUserId = ownerUserId;
        }
    }
} 