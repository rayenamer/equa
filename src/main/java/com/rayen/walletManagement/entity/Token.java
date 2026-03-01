package com.rayen.walletManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @Column(nullable = false)
    private Double value = 0.0;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private Double conversionRate = 1.0;

    private Integer totalSupply = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    @JsonIgnore
    private Wallet wallet;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean transfer(Long recipientId, Double amount) {
        if (amount <= 0 || value < amount) {
            return false;
        }
        value -= amount;
        return true;
    }

    public Double getConvertedValue() {
        return value * conversionRate;
    }
}
