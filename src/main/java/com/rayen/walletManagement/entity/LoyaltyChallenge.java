package com.rayen.walletManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loyalty_challenges")
public class LoyaltyChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String challengeType; // TRANSACTION_COUNT, TRANSACTION_VOLUME, STREAK, DEPOSIT_COUNT

    @Column(nullable = false)
    private Double targetValue; // e.g. 10 transactions, 5000 EUR volume, 7 day streak

    @Column(nullable = false)
    private Integer rewardPoints;

    @Column(nullable = false)
    private Double cashbackBonus; // bonus cashback percentage when challenge is completed

    private String badgeName; // badge awarded on completion

    @Column(nullable = false)
    private Boolean active = true;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
