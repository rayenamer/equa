package com.rayen.loanManagement.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record LoanCreateRequest(
    @NotNull Long userId,
    @Positive float amount,
    @Positive float interestRate,
    @NotNull @Positive Integer durationMonths,
    @NotBlank String status,
    @FutureOrPresent LocalDate dueDate
) {}
