package com.rayen.loanManagement.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record DashboardResponse(
    long totalLoans,
    long approvedLoans,
    long pendingLoans,
    long defaultedLoans,
    long completedLoans,
    BigDecimal totalLoanAmount,
    BigDecimal averageLoanAmount,
    BigDecimal totalInterestProjected,
    long totalRepayments,
    long onTimeRepayments,
    long lateRepayments,
    long missedRepayments,
    BigDecimal totalAmountCollected,
    BigDecimal totalPenaltiesCollected,
    double onTimePaymentRate,
    long totalUsers,
    double averageCreditScore,
    long lowRiskUsers,
    long mediumRiskUsers,
    long highRiskUsers,
    long totalInsurances,
    long activeInsurances,
    long cancelledInsurances,
    BigDecimal totalPremiumsCollected,
    Map<String, Long> insuranceByType,
    BigDecimal defaultRate,
    BigDecimal completionRate,
    LocalDate generatedAt
) {}
