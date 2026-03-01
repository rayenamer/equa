package com.rayen.loanManagement.model;

import java.time.LocalDate;

public record MicroInsuranceResponse(
    Long insuranceId,
    Long loanId,
    Long userId,
    String type,
    Double coverageAmount,
    Double premium,
    String status,
    LocalDate startDate,
    LocalDate endDate,
    String protectionLevel
) {
    public static String computeProtectionLevel(Double coverageAmount, double loanAmount) {
        if (coverageAmount == null) return "UNKNOWN";
        if (coverageAmount >= loanAmount * 1.2) return "FULL PROTECTION";
        if (coverageAmount >= loanAmount * 0.8) return "PARTIAL PROTECTION";
        return "MINIMAL PROTECTION";
    }
}
