package com.diakonbank.authservice.dto.request;

import com.diakonbank.authservice.enums.SupportedBank;

public class ConnectBankRequest {
    private String bankLogin;
    private String bankPassword;
    private SupportedBank bank;

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

    public SupportedBank getBank() {
        return bank;
    }

    public void setBank(SupportedBank bank) {
        this.bank = bank;
    }
} 