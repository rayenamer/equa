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
public class AnalyticsSummary {
    private long totalWallets;
    private BigDecimal totalBalance;
    private BigDecimal averageBalance;
    private Map<CurrencyCode, BigDecimal> currencyDistribution;
    private Map<LoyaltyTier, Long> tierDistribution;
    private long activeWallets;
    private long suspendedWallets;
    private Map<FraudRiskLevel, Long> fraudRiskDistribution;
}
