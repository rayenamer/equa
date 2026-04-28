package com.rayen.loanManagement.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record LoanDecisionRequest(
    @NotNull Long loanId,
    @NotNull Long userId,
    @NotNull @Positive BigDecimal monthlyIncome
) {}
