package com.rayen.loanManagement.model;

import java.math.BigDecimal;

public record RepaymentSummary(
    Long loanId,
    BigDecimal totalPaid,
    BigDecimal totalPending,
    BigDecimal totalPenalties,
    int paidPeriods,
    int remainingPeriods,
    double completionPercent,
    String loanStatus
) {}
