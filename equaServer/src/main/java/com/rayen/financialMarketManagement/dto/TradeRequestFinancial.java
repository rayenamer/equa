package com.rayen.financialMarketManagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TradeRequestFinancial {
    // TradeRequestFinancial.java
    @NotNull
    private BigDecimal amountEqua;   // EQUA to spend (buy) or EQUA value to sell
    private Long assetId;
}

