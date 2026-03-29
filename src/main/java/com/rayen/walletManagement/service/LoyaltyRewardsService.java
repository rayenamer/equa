package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoyaltyRewardsService {

    @Autowired
    private WalletRepository walletRepository;

    // Points earned per 100 units of transaction
    private static final int POINTS_PER_100 = 10;

    // Tier thresholds
    private static final int SILVER_THRESHOLD = 500;
    private static final int GOLD_THRESHOLD = 2000;
    private static final int PLATINUM_THRESHOLD = 5000;

    // Tier bonuses (multiplier on points earned)
    private static final Map<String, Double> TIER_MULTIPLIERS = new HashMap<>();

    static {
        TIER_MULTIPLIERS.put("BRONZE", 1.0);
        TIER_MULTIPLIERS.put("SILVER", 1.5);
        TIER_MULTIPLIERS.put("GOLD", 2.0);
        TIER_MULTIPLIERS.put("PLATINUM", 3.0);
    }

    // Redemption rate: 1 point = 0.10 EUR
    private static final Double POINT_VALUE = 0.10;

    // ==================== LOYALTY OPERATIONS ====================

    @Transactional
    public Map<String, Object> earnPoints(Long walletId, Double transactionAmount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));

        if (!"ACTIVE".equals(wallet.getStatus())) {
            throw new RuntimeException("Wallet is not active");
        }

        // Calculate points earned
        int basePoints = (int) (transactionAmount / 100.0) * POINTS_PER_100;
        Double multiplier = TIER_MULTIPLIERS.getOrDefault(wallet.getLoyaltyTier(), 1.0);
        int earnedPoints = (int) (basePoints * multiplier);

        wallet.setLoyaltyPoints(wallet.getLoyaltyPoints() + earnedPoints);

        // Check tier upgrade
        String oldTier = wallet.getLoyaltyTier();
        updateTier(wallet);
        String newTier = wallet.getLoyaltyTier();

        wallet.getTransactionHistory().add(
                LocalDateTime.now() + " | LOYALTY_EARNED | +" + earnedPoints + " points"
                        + " | multiplier: x" + multiplier + " (" + oldTier + ")"
                        + (oldTier.equals(newTier) ? "" : " | TIER_UPGRADE: " + oldTier + " → " + newTier)
        );

        walletRepository.save(wallet);

        Map<String, Object> result = new HashMap<>();
        result.put("walletId", walletId);
        result.put("transactionAmount", transactionAmount);
        result.put("basePoints", basePoints);
        result.put("tierMultiplier", multiplier);
        result.put("earnedPoints", earnedPoints);
        result.put("totalPoints", wallet.getLoyaltyPoints());
        result.put("currentTier", wallet.getLoyaltyTier());
        result.put("tierUpgraded", !oldTier.equals(newTier));
        if (!oldTier.equals(newTier)) {
            result.put("previousTier", oldTier);
        }
        return result;
    }

    @Transactional
    public Map<String, Object> redeemPoints(Long walletId, Integer pointsToRedeem) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));

        if (!"ACTIVE".equals(wallet.getStatus())) {
            throw new RuntimeException("Wallet is not active");
        }

        if (pointsToRedeem <= 0) {
            throw new RuntimeException("Points to redeem must be positive");
        }

        if (wallet.getLoyaltyPoints() < pointsToRedeem) {
            throw new RuntimeException("Insufficient loyalty points. Available: " + wallet.getLoyaltyPoints()
                    + ", Requested: " + pointsToRedeem);
        }

        Double cashValue = pointsToRedeem * POINT_VALUE;

        wallet.setLoyaltyPoints(wallet.getLoyaltyPoints() - pointsToRedeem);
        wallet.setBalance(wallet.getBalance() + cashValue);

        // Check tier downgrade
        String oldTier = wallet.getLoyaltyTier();
        updateTier(wallet);
        String newTier = wallet.getLoyaltyTier();

        wallet.getTransactionHistory().add(
                LocalDateTime.now() + " | LOYALTY_REDEEMED | -" + pointsToRedeem + " points"
                        + " | +" + cashValue + " " + wallet.getCurrency() + " credited"
                        + (oldTier.equals(newTier) ? "" : " | TIER_CHANGE: " + oldTier + " → " + newTier)
        );

        walletRepository.save(wallet);

        Map<String, Object> result = new HashMap<>();
        result.put("walletId", walletId);
        result.put("pointsRedeemed", pointsToRedeem);
        result.put("cashValue", cashValue);
        result.put("remainingPoints", wallet.getLoyaltyPoints());
        result.put("newBalance", wallet.getBalance());
        result.put("currentTier", wallet.getLoyaltyTier());
        return result;
    }

    public Map<String, Object> getLoyaltyStatus(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));

        Integer points = wallet.getLoyaltyPoints();
        String tier = wallet.getLoyaltyTier();
        String nextTier = getNextTier(tier);
        Integer pointsToNextTier = getPointsToNextTier(points, tier);

        Map<String, Object> result = new HashMap<>();
        result.put("walletId", walletId);
        result.put("currentPoints", points);
        result.put("currentTier", tier);
        result.put("tierMultiplier", TIER_MULTIPLIERS.get(tier));
        result.put("pointValue", POINT_VALUE);
        result.put("cashEquivalent", points * POINT_VALUE);
        result.put("nextTier", nextTier);
        result.put("pointsToNextTier", pointsToNextTier);

        // Tier benefits
        Map<String, Object> benefits = new HashMap<>();
        benefits.put("BRONZE", "Base points (x1.0)");
        benefits.put("SILVER", "1.5x points + priority support");
        benefits.put("GOLD", "2x points + reduced fees + priority support");
        benefits.put("PLATINUM", "3x points + zero fees + VIP support + exclusive offers");
        result.put("tierBenefits", benefits);

        return result;
    }

    // ==================== HELPERS ====================

    private void updateTier(Wallet wallet) {
        int points = wallet.getLoyaltyPoints();
        if (points >= PLATINUM_THRESHOLD) {
            wallet.setLoyaltyTier("PLATINUM");
        } else if (points >= GOLD_THRESHOLD) {
            wallet.setLoyaltyTier("GOLD");
        } else if (points >= SILVER_THRESHOLD) {
            wallet.setLoyaltyTier("SILVER");
        } else {
            wallet.setLoyaltyTier("BRONZE");
        }
    }

    private String getNextTier(String currentTier) {
        return switch (currentTier) {
            case "BRONZE" -> "SILVER";
            case "SILVER" -> "GOLD";
            case "GOLD" -> "PLATINUM";
            case "PLATINUM" -> "PLATINUM (MAX)";
            default -> "UNKNOWN";
        };
    }

    private Integer getPointsToNextTier(Integer currentPoints, String currentTier) {
        return switch (currentTier) {
            case "BRONZE" -> Math.max(0, SILVER_THRESHOLD - currentPoints);
            case "SILVER" -> Math.max(0, GOLD_THRESHOLD - currentPoints);
            case "GOLD" -> Math.max(0, PLATINUM_THRESHOLD - currentPoints);
            case "PLATINUM" -> 0;
            default -> 0;
        };
    }
}
