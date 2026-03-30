package com.rayen.walletManagement.model;

import java.math.BigDecimal;

public enum LoyaltyTier {
    BRONZE(BigDecimal.valueOf(0), BigDecimal.valueOf(1.0)),
    SILVER(BigDecimal.valueOf(1_000), BigDecimal.valueOf(1.1)),
    GOLD(BigDecimal.valueOf(5_000), BigDecimal.valueOf(1.2)),
    PLATINUM(BigDecimal.valueOf(20_000), BigDecimal.valueOf(1.3));

    private final BigDecimal pointsThreshold;
    private final BigDecimal multiplier;

    LoyaltyTier(BigDecimal pointsThreshold, BigDecimal multiplier) {
        this.pointsThreshold = pointsThreshold;
        this.multiplier = multiplier;
    }

    public BigDecimal getPointsThreshold() {
        return pointsThreshold;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }
}
