package com.rayen.financialMarketManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PortfolioItemResponseFinancial {
    private String assetName;
    private String ticker;
    private BigDecimal quantity;
    private BigDecimal avgBuyPriceEqua;
    private BigDecimal currentPriceEqua;
    private BigDecimal totalValueEqua;
    private BigDecimal pnlEqua;
}

