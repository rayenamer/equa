package com.rayen.loanManagement.model;

import java.time.LocalDate;

public record CreditScoreResponse(
    Long scoreId,
    Long userId,
    Integer score,
    Integer latePayments,
    LocalDate lastUpdated,
    String riskLevel
) {
    public static String computeRiskLevel(Integer score) {
        if (score == null) return "UNKNOWN";
        if (score >= 700) return "LOW RISK";
        if (score >= 500) return "MEDIUM RISK";
        return "HIGH RISK";
    }
}
