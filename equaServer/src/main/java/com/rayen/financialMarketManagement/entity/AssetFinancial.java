package com.rayen.financialMarketManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "asset_financial")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssetFinancial {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String ticker;

    @Enumerated(EnumType.STRING)
    private AssetCategory category; // CRYPTO, AGRICULTURE, TECHNOLOGY, REAL_ESTATE

    private String description;
    private String logoUrl;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal currentPriceEqua;   // price in EQUA

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal totalSupply;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal circulatingSupply;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal volume24h = BigDecimal.ZERO;

    private Long creatorUserId;

    @Enumerated(EnumType.STRING)
    private AssetStatus status = AssetStatus.PENDING; // PENDING, ACTIVE, REJECTED

    private boolean verified = false;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

