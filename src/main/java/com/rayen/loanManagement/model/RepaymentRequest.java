package com.rayen.loanManagement.model;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RepaymentRequest(
    @NotNull Long loanId,
    @NotNull Long userId,
    @NotNull Integer periodNumber,
    @NotNull BigDecimal amountPaid,
    @NotNull LocalDate paymentDate
) {}
