package com.rayen.loanManagement.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MicroInsuranceRequest(
    @NotNull Long loanId,
    @NotNull Long userId,
    @NotBlank String type   // LIFE | DISABILITY | UNEMPLOYMENT
) {}
