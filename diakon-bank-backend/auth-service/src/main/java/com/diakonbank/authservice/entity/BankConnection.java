package com.diakonbank.authservice.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity
@Table(name = "bank_connections")
public class BankConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String bankLogin;
    private String encryptedBankPassword;
    @Column(name = "bank_identifier", nullable = false)
    private String bankIdentifier;
    @Column(nullable = false)
    private String status;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getBankLogin() {
        return bankLogin;
    }
    public void setBankLogin(String bankLogin) {
        this.bankLogin = bankLogin;
    }
    public String getEncryptedBankPassword() {
        return encryptedBankPassword;
    }
    public void setEncryptedBankPassword(String encryptedBankPassword) {
        this.encryptedBankPassword = encryptedBankPassword;
    }
    public String getBankIdentifier() {
        return bankIdentifier;
    }
    public void setBankIdentifier(String bankIdentifier) {
        this.bankIdentifier = bankIdentifier;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}