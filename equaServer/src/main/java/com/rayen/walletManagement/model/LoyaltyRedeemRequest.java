package com.rayen.walletManagement.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoyaltyRedeemRequest {
    private BigDecimal points;
}
