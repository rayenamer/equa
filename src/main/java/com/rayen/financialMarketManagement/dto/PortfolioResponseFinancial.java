package com.rayen.financialMarketManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponseFinancial {
    private Long userId;
    private List<PortfolioItemResponseFinancial> items;
    private BigDecimal totalValueEqua;
}

