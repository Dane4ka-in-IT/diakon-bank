package com.diakonbank.bankapi.service.controller;

import com.diakonbank.bankapi.service.client.BankApiClient;
import com.diakonbank.bankapi.service.dto.request.CredentialValidationRequest;
import com.diakonbank.bankapi.service.dto.request.UserSyncRequest;
import com.diakonbank.bankapi.service.service.BankIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalApiController {

    private final BankApiClient bankApiClient;
    private final BankIntegrationService bankIntegrationService;

    @PostMapping("/validate-credentials")
    public Mono<ResponseEntity<Boolean>> validateCredentials(@RequestBody CredentialValidationRequest request) {
        return bankApiClient.getAccessToken(request.getLogin(), request.getPassword(), request.getBank())
                .map(token -> ResponseEntity.ok(true))
                .onErrorResume(e -> Mono.just(ResponseEntity.ok(false)));
    }

    @PostMapping("/sync-user")
    public ResponseEntity<Void> syncUser(@RequestBody UserSyncRequest request) {
        bankIntegrationService.syncUserData(request.getLogin(), request.getPassword(), request.getOwnerUserId(), request.getBank());
        return ResponseEntity.accepted().build();
    }
} 