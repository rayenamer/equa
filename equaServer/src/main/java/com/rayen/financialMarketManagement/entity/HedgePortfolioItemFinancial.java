package com.rayen.financialMarketManagement.entity;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "hedge_portfolio_item_financial")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HedgePortfolioItemFinancial {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id") private HedgePortfolioFinancial portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id") private AssetFinancial asset;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal avgBuyPriceEqua = BigDecimal.ZERO;
}

