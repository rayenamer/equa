package com.rayen.financialMarketManagement.dto;
import com.rayen.financialMarketManagement.entity.AssetCategory;
import com.rayen.financialMarketManagement.entity.AssetStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AssetResponseFinancial {
    private Long id;
    private String name;
    private String ticker;
    private AssetCategory category;
    private String description;
    private String logoUrl;
    private BigDecimal currentPriceEqua;
    private BigDecimal totalSupply;
    private BigDecimal circulatingSupply;
    private BigDecimal volume24h;
    private AssetStatus status;
    private boolean verified;
    private LocalDateTime createdAt;
}

