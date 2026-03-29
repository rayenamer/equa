package com.rayen.walletManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cashback_rewards")
public class CashbackReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long walletId;

    @Column(nullable = false)
    private Double transactionAmount;

    @Column(nullable = false)
    private Double cashbackPercentage;

    @Column(nullable = false)
    private Double cashbackAmount;

    @Column(nullable = false)
    private String rewardType; // TRANSACTION_CASHBACK, STREAK_BONUS, CHALLENGE_REWARD, TIER_BONUS

    private String description;

    private Boolean creditedAsTokens = false; // true = tokens, false = balance

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
