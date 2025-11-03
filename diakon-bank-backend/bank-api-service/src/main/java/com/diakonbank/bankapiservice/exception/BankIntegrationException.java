package com.diakonbank.bankapiservice.exception;

public class BankIntegrationException extends RuntimeException {

    public BankIntegrationException(String message) {
        super(message);
    }

    public BankIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
