package com.rayen.loanManagement.model;

import java.math.BigDecimal;
import java.util.List;

public record UserDashboardResponse(
    Long userId,
    List<LoanResponse> loans,
    long totalUserLoans,
    BigDecimal totalBorrowed,
    Integer creditScore,
    String riskLevel,
    Integer latePayments,
    BigDecimal totalPaid,
    BigDecimal totalPenalties,
    double personalOnTimeRate,
    List<MicroInsuranceResponse> activeInsurances,
    BigDecimal totalPremiums,
    String profileSummary
) {}
