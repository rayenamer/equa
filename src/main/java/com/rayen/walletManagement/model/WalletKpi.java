package com.rayen.walletManagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletKpi {
    private Long walletId;
    private BigDecimal mainBalance;
    private BigDecimal aggregateNetWorth;
    private BigDecimal loyaltyPoints;
    private LoyaltyTier loyaltyTier;
    private Map<CurrencyCode, BigDecimal> currencyExposure;
    private BigDecimal balanceEvolutionIndicator;
    private Integer completedAchievements;
    private Integer completedChallenges;
    private FraudRiskLevel fraudRiskLevel;
}
