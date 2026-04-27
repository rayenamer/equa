package com.rayen.financialMarketManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryResponseFinancial {
    private BigDecimal priceEqua;
    private LocalDateTime recordedAt;
}

