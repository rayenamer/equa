package com.rayen.walletManagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {
    private Long id;
    private String publicKey;
    private String status;
    private BigDecimal balance;
    private BigDecimal equaAmount;
    private String ownerEmail;
    private LoyaltyTier loyaltyTier;
    private BigDecimal loyaltyPoints;
    private String fraudRiskLevel;
    private Set<String> achievements;
    private Set<String> completedChallenges;
    private Map<CurrencyCode, BigDecimal> currencyBalances;
}
