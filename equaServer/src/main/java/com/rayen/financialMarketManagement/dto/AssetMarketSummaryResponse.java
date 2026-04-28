package com.rayen.financialMarketManagement.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetMarketSummaryResponse {
    private BigDecimal totalMarketCap;      // sum of currentPriceEqua × circulatingSupply
    private BigDecimal totalVolume24h;      // sum of volume24h
    private BigDecimal totalSupply;         // sum of totalSupply
    private BigDecimal totalCirculating;    // sum of circulatingSupply
    private int assetCount;

}
