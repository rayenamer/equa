package com.rayen.loanManagement.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AmortizationRowResponse(
    Integer periodNumber,
    LocalDate periodDate,
    BigDecimal startingBalance,
    BigDecimal interest,
    BigDecimal principal,
    BigDecimal payment,
    BigDecimal endingBalance,
    String status
) {}
