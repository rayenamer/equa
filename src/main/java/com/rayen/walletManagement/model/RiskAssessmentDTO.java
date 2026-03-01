package com.rayen.walletManagement.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RiskAssessmentDTO {

    private Long customerId;
    private Integer creditScore;
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    private Double maxAllowedTransaction;
    private Double totalAssetValue;
    private Double walletBalance;
    private Integer totalTransactions;
    private Integer latePayments;
    private Double repaymentRate;
    private Double predictedDefaultProbability;
    private String recommendation; // APPROVE, REVIEW, DENY
    private Map<String, Double> financialIndicators;
    private LocalDateTime assessmentDate;
}
