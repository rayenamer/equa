package com.rayen.financialMarketManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "asset_price_history_financial",
        indexes = @Index(columnList = "asset_id, recorded_at"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssetPriceHistoryFinancial {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id") private AssetFinancial asset;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal priceEqua;

    @Column(updatable = false)
    private LocalDateTime recordedAt = LocalDateTime.now();
}

