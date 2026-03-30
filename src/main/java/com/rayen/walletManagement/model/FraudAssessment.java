package com.rayen.walletManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudAssessment {
    private BigDecimal riskScore;
    private FraudRiskLevel level;
}
