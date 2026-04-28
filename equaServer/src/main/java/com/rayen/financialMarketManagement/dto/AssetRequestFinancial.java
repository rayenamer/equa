package com.rayen.financialMarketManagement.dto;
import com.rayen.financialMarketManagement.entity.AssetCategory;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AssetRequestFinancial {
    private String name;
    private String ticker;
    private AssetCategory category;
    private String description;
    private String logoUrl;
    private BigDecimal initialPriceEqua;
    private BigDecimal totalSupply;
    //
    private BigDecimal volume24h;
}
