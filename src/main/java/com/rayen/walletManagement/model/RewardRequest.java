package com.rayen.walletManagement.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RewardRequest {
    private RewardType rewardType;
    private BigDecimal amount;
}
