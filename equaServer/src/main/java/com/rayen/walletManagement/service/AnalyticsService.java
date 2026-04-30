package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.model.AnalyticsSummary;
import com.rayen.walletManagement.model.CurrencyCode;
import com.rayen.walletManagement.model.FraudRiskLevel;
import com.rayen.walletManagement.model.LoyaltyTier;
import com.rayen.walletManagement.model.WalletKpi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final GamificationService gamificationService;

    public AnalyticsSummary computeGlobalSummary(List<Wallet> wallets) {
        long totalWallets = wallets.size();
        BigDecimal totalBalance = wallets.stream()
                .map(this::computeAggregateBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageBalance = totalWallets > 0
                ? totalBalance.divide(BigDecimal.valueOf(totalWallets), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;

        Map<CurrencyCode, BigDecimal> currencyDistribution = new EnumMap<>(CurrencyCode.class);
        for (CurrencyCode currency : CurrencyCode.values()) {
            currencyDistribution.put(currency, BigDecimal.ZERO);
        }
        for (Wallet wallet : wallets) {
            if (wallet.getDeviseWallet() != null) {
                wallet.getDeviseWallet().getBalances().forEach((currency, amount) -> {
                    BigDecimal existing = currencyDistribution.getOrDefault(currency, BigDecimal.ZERO);
                    currencyDistribution.put(currency, existing.add(amount == null ? BigDecimal.ZERO : amount));
                });
            }
        }

        Map<LoyaltyTier, Long> tierDistribution = wallets.stream()
                .collect(Collectors.groupingBy(Wallet::getLoyaltyTier, Collectors.counting()));

        long activeWallets = wallets.stream()
                .filter(wallet -> wallet.getStatus() != null && wallet.getStatus().equalsIgnoreCase("ACTIVE"))
                .count();
        long suspendedWallets = wallets.stream()
                .filter(wallet -> wallet.getStatus() != null && wallet.getStatus().equalsIgnoreCase("SUSPENDED"))
                .count();

        Map<FraudRiskLevel, Long> fraudRiskDistribution = wallets.stream()
                .collect(Collectors.groupingBy(Wallet::getFraudRiskLevel, Collectors.counting()));

        return AnalyticsSummary.builder()
                .totalWallets(totalWallets)
                .totalBalance(totalBalance)
                .averageBalance(averageBalance)
                .currencyDistribution(currencyDistribution)
                .tierDistribution(tierDistribution)
                .activeWallets(activeWallets)
                .suspendedWallets(suspendedWallets)
                .fraudRiskDistribution(fraudRiskDistribution)
                .build();
    }

    public WalletKpi computeWalletKpi(Wallet wallet) {
        BigDecimal aggregateBalance = computeAggregateBalance(wallet);
        Map<CurrencyCode, BigDecimal> currencyExposure = new EnumMap<>(CurrencyCode.class);
        if (wallet.getDeviseWallet() != null) {
            wallet.getDeviseWallet().getBalances().forEach((currency, amount) -> currencyExposure.put(currency, amount == null ? BigDecimal.ZERO : amount));
        }

        BigDecimal balanceEvolutionIndicator = BigDecimal.valueOf(wallet.getRecentBalanceChanges() == null ? 0 : wallet.getRecentBalanceChanges());

        return WalletKpi.builder()
                .walletId(wallet.getWalletId())
                .mainBalance(BigDecimal.valueOf(wallet.getEquaAmount()))
                .aggregateNetWorth(aggregateBalance)
                .loyaltyPoints(wallet.getLoyaltyPoints())
                .loyaltyTier(wallet.getLoyaltyTier())
                .currencyExposure(currencyExposure)
                .balanceEvolutionIndicator(balanceEvolutionIndicator)
                .completedAchievements(wallet.getAchievements().size())
                .completedChallenges(wallet.getCompletedChallenges().size())
                .fraudRiskLevel(wallet.getFraudRiskLevel())
                .build();
    }

    private BigDecimal computeAggregateBalance(Wallet wallet) {
        BigDecimal mainBalance = BigDecimal.valueOf(wallet.getEquaAmount());
        if (wallet.getDeviseWallet() == null) {
            return mainBalance;
        }
        BigDecimal aggregate = mainBalance;
        for (Map.Entry<CurrencyCode, BigDecimal> entry : wallet.getDeviseWallet().getBalances().entrySet()) {
            if (entry.getValue() != null) {
                aggregate = aggregate.add(entry.getKey().getEurRate().multiply(entry.getValue()));
            }
        }
        return aggregate;
    }
}
