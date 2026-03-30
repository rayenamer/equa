package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.model.AchievementType;
import com.rayen.walletManagement.model.ChallengeType;
import com.rayen.walletManagement.model.CurrencyCode;
import com.rayen.walletManagement.model.RewardType;
import com.rayen.walletManagement.model.WalletOperationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GamificationService {

    public void evaluateAchievements(Wallet wallet, WalletOperationType operationType) {
        if (wallet.getBalance() > 0 && !wallet.getAchievements().contains(AchievementType.FIRST_WALLET_FUNDING.name())) {
            wallet.getAchievements().add(AchievementType.FIRST_WALLET_FUNDING.name());
        }

        if (operationType == WalletOperationType.CONVERSION) {
            wallet.getAchievements().add(AchievementType.FIRST_CURRENCY_CONVERSION.name());
        }

        if (hasMultipleCurrencies(wallet)) {
            wallet.getAchievements().add(AchievementType.HOLDING_MULTIPLE_CURRENCIES.name());
        }

        if (computeAggregateNetWorth(wallet).compareTo(BigDecimal.valueOf(10_000)) >= 0) {
            wallet.getAchievements().add(AchievementType.REACHED_HIGH_BALANCE.name());
        }

        if (wallet.getLoyaltyTier().name().equals("PLATINUM")) {
            wallet.getAchievements().add(AchievementType.REACHED_PLATINUM_TIER.name());
        }
    }

    public void evaluateChallenges(Wallet wallet) {
        if (wallet.getBalance() >= 500) {
            wallet.getCompletedChallenges().add(ChallengeType.DAILY_MINIMUM_BALANCE.name());
        }

        if (computeAggregateNetWorth(wallet).compareTo(BigDecimal.valueOf(5_000)) >= 0) {
            wallet.getCompletedChallenges().add(ChallengeType.WEEKLY_VALUE_GROWTH.name());
        }

        if (hasMultipleCurrencies(wallet)) {
            wallet.getCompletedChallenges().add(ChallengeType.MULTI_CURRENCY_USE.name());
        }

        if (wallet.getRecentBalanceChanges() != null && wallet.getRecentBalanceChanges() >= 3) {
            wallet.getCompletedChallenges().add(ChallengeType.CONSISTENT_ACTIVITY.name());
        }
    }

    public void applyReward(Wallet wallet, RewardType rewardType, BigDecimal amount) {
        switch (rewardType) {
            case BONUS_POINTS -> wallet.setLoyaltyPoints(wallet.getLoyaltyPoints().add(amount));
            case CASHBACK -> wallet.setBalance(wallet.getBalance() + amount.floatValue());
            case TIER_BOOST -> wallet.setLoyaltyPoints(wallet.getLoyaltyPoints().add(BigDecimal.valueOf(500)));
            default -> throw new IllegalArgumentException("Unknown reward type");
        }
        log.info("Applied {} reward of {} to wallet {}", rewardType, amount, wallet.getWalletId());
    }

    public BigDecimal computeAggregateNetWorth(Wallet wallet) {
        BigDecimal mainBalance = BigDecimal.valueOf(wallet.getBalance());
        if (wallet.getDeviseWallet() == null) {
            return mainBalance;
        }
        BigDecimal total = mainBalance;
        for (Map.Entry<CurrencyCode, BigDecimal> entry : wallet.getDeviseWallet().getBalances().entrySet()) {
            if (entry.getValue() != null) {
                total = total.add(entry.getKey().getEurRate().multiply(entry.getValue()));
            }
        }
        return total;
    }

    private boolean hasMultipleCurrencies(Wallet wallet) {
        if (wallet.getDeviseWallet() == null) {
            return false;
        }
        return wallet.getDeviseWallet().getBalances().entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) > 0)
                .count() >= 2;
    }
}
