package com.rayen.walletManagement.entity;

import com.rayen.userManaement.entity.User;
import com.rayen.walletManagement.model.FraudRiskLevel;
import com.rayen.walletManagement.model.LoyaltyTier;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @Builder.Default
    private Float balance = 0f;

    private String publicKey;
    private String status;

    @Builder.Default
    private Float equaAmount = 0f;

    @Builder.Default
    private BigDecimal loyaltyPoints = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LoyaltyTier loyaltyTier = LoyaltyTier.BRONZE;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FraudRiskLevel fraudRiskLevel = FraudRiskLevel.LOW;

    @Builder.Default
    private Integer recentBalanceChanges = 0;

    @Builder.Default
    private Integer recentConversionCount = 0;

    private LocalDateTime lastActivityAt;
    private LocalDateTime lastBalanceChangeAt;
    private LocalDateTime lastConversionAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "wallet_achievements", joinColumns = @JoinColumn(name = "wallet_id"))
    @Column(name = "achievement")
    @Builder.Default
    private Set<String> achievements = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "wallet_challenges", joinColumns = @JoinColumn(name = "wallet_id"))
    @Column(name = "challenge")
    @Builder.Default
    private Set<String> completedChallenges = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "devise_wallet_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private DeviseWallet deviseWallet;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
