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

    public Mono<Boolean> validateCredentials(String login, String password, String bank) {
        return bankApiClient.post()
                .uri("/validate-credentials")
                .bodyValue(new CredentialValidationRequest(login, password, bank))
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Void> syncUser(String login, String password, Long ownerUserId, String bank) {
        return bankApiClient.post()
                .uri("/sync-user")
                .bodyValue(new UserSyncRequest(login, password, ownerUserId, bank))
                .retrieve()
                .bodyToMono(Void.class);
    }

    // DTOs for requests
    private static class CredentialValidationRequest {
        public String login;
        public String password;
        public String bank;

        public CredentialValidationRequest(String login, String password, String bank) {
            this.login = login;
            this.password = password;
            this.bank = bank;
        }
    }

    private static class UserSyncRequest {
        public String login;
        public String password;
        public Long ownerUserId;
        public String bank;

        public UserSyncRequest(String login, String password, Long ownerUserId, String bank) {
            this.login = login;
            this.password = password;
            this.ownerUserId = ownerUserId;
            this.bank = bank;
        }
    }
} 