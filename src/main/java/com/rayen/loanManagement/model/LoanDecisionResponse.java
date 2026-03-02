package com.rayen.loanManagement.model;

import java.math.BigDecimal;

public record LoanDecisionResponse(
    Long loanId,
    Long userId,
    String decision,           // APPROVED / MANUAL_REVIEW / REJECTED
    int totalScore,
    int creditScorePoints,
    int debtRatioPoints,
    int amountRatioPoints,
    int latePaymentPoints,
    BigDecimal monthlyPayment,
    BigDecimal debtRatio,
    String reason,
    String recommendation
) {}
