package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.model.LoyaltyTier;
import com.rayen.walletManagement.model.WalletOperationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class LoyaltyService {

    public void recordActivity(Wallet wallet, BigDecimal amount, WalletOperationType operationType) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal basePoints = amount.divide(BigDecimal.TEN, 0, BigDecimal.ROUND_HALF_UP);
        BigDecimal multiplier = wallet.getLoyaltyTier().getMultiplier();
        BigDecimal earned = basePoints.multiply(multiplier);

        if (operationType == WalletOperationType.CONVERSION || operationType == WalletOperationType.TRANSFER) {
            earned = earned.add(BigDecimal.valueOf(5));
        }

        BigDecimal updated = wallet.getLoyaltyPoints().add(earned);
        wallet.setLoyaltyPoints(updated);
        refreshTier(wallet);

        log.info("Loyalty updated for wallet {}: earned {} points, total {}", wallet.getWalletId(), earned, updated);
    }

    public void refreshTier(Wallet wallet) {
        LoyaltyTier nextTier = wallet.getLoyaltyTier();
        for (LoyaltyTier tier : LoyaltyTier.values()) {
            if (wallet.getLoyaltyPoints().compareTo(tier.getPointsThreshold()) >= 0) {
                nextTier = tier;
            }
        }
        wallet.setLoyaltyTier(nextTier);
    }

    public void redeemPoints(Wallet wallet, BigDecimal pointsToRedeem) {
        if (pointsToRedeem == null || pointsToRedeem.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Redeem amount must be positive");
        }
        if (wallet.getLoyaltyPoints().compareTo(pointsToRedeem) < 0) {
            throw new IllegalArgumentException("Not enough loyalty points");
        }

        BigDecimal creditValue = pointsToRedeem.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
        wallet.setLoyaltyPoints(wallet.getLoyaltyPoints().subtract(pointsToRedeem));
        wallet.setEquaAmount(wallet.getEquaAmount() + creditValue.floatValue());
        refreshTier(wallet);
        log.info("Redeemed {} points into {} EUR for wallet {}", pointsToRedeem, creditValue, wallet.getWalletId());
    }
}
