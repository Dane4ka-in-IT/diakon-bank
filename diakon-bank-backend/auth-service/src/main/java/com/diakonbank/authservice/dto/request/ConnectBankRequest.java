package com.diakonbank.authservice.dto.request;

public class ConnectBankRequest {
    private String bankLogin;
    private String bankPassword;

    // Getters and setters
    public String getBankLogin() {
        return bankLogin;
    }

    public void setBankLogin(String bankLogin) {
        this.bankLogin = bankLogin;
    }

    public String getBankPassword() {
        return bankPassword;
    }

    public void setBankPassword(String bankPassword) {
        this.bankPassword = bankPassword;
    }
} 