package com.rayen.loanManagement.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanResponse(
    Long loanId,
    Long userId,
    float amount,
    float interestRate,
    Integer durationMonths,
    String status,
    LocalDate startDate,
    LocalDate dueDate,
    BigDecimal monthlyPayment
) {}
