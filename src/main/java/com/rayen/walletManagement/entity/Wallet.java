package com.rayen.walletManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    private Double balance = 0.0;

    @Column(nullable = false, unique = true)
    private Long customerId;

    @Column(unique = true)
    private String publicKey;

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE, SUSPENDED, CLOSED

    @Column(nullable = false)
    private String currency = "EUR"; // EUR, USD, GBP, BTC, ETH

    private Integer loyaltyPoints = 0;
    private String loyaltyTier = "BRONZE"; // BRONZE, SILVER, GOLD, PLATINUM

    @ElementCollection
    @CollectionTable(name = "wallet_transaction_history", joinColumns = @JoinColumn(name = "wallet_id"))
    @Column(name = "transaction")
    private List<String> transactionHistory = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens = new ArrayList<>();

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asset> assets = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean sendTokens(Double amount, String recipient) {
        if (amount <= 0 || balance < amount || !"ACTIVE".equals(status)) {
            return false;
        }
        balance -= amount;
        transactionHistory.add(LocalDateTime.now() + " | SEND | -" + amount + " | to: " + recipient);
        return true;
    }

    public void receiveTokens(Double amount) {
        if (amount > 0 && "ACTIVE".equals(status)) {
            balance += amount;
            transactionHistory.add(LocalDateTime.now() + " | RECEIVE | +" + amount);
        }
    }

    public boolean deposit(Double amount) {
        if (amount <= 0 || !"ACTIVE".equals(status)) {
            return false;
        }
        balance += amount;
        transactionHistory.add(LocalDateTime.now() + " | DEPOSIT | +" + amount);
        return true;
    }

    public boolean withdraw(Double amount) {
        if (amount <= 0 || balance < amount || !"ACTIVE".equals(status)) {
            return false;
        }
        balance -= amount;
        transactionHistory.add(LocalDateTime.now() + " | WITHDRAW | -" + amount);
        return true;
    }
}
