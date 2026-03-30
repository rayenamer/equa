package com.rayen.walletManagement.model;

import java.math.BigDecimal;

public enum CurrencyCode {
    EUR(BigDecimal.valueOf(1.0)),
    USD(BigDecimal.valueOf(1.1)),
    TND(BigDecimal.valueOf(3.3)),
    BTC(BigDecimal.valueOf(29000.0));

    private final BigDecimal eurRate;

    CurrencyCode(BigDecimal eurRate) {
        this.eurRate = eurRate;
    }

    public BigDecimal getEurRate() {
        return eurRate;
    }

    public BigDecimal convertTo(CurrencyCode target, BigDecimal amount) {
        BigDecimal amountInEur = amount.multiply(eurRate);
        return amountInEur.divide(target.eurRate, 8, BigDecimal.ROUND_HALF_UP);
    }
}
