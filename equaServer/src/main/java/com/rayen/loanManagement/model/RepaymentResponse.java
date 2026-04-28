package com.rayen.loanManagement.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RepaymentResponse(
    Long repaymentId,
    Long loanId,
    Long userId,
    Integer periodNumber,
    BigDecimal amountPaid,
    BigDecimal expectedAmount,
    BigDecimal penaltyAmount,
    LocalDate dueDate,
    LocalDate paymentDate,
    String paymentStatus,
    String message
) {}
