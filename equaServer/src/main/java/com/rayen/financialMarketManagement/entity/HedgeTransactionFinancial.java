package com.rayen.financialMarketManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "hedge_transaction_financial")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HedgeTransactionFinancial {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id") private AssetFinancial asset;

    @Enumerated(EnumType.STRING)
    private TradeType type; // BUY or SELL

    @Column(nullable = false, precision = 18, scale = 6) private BigDecimal quantity;
    @Column(nullable = false, precision = 18, scale = 6) private BigDecimal pricePerUnitEqua;
    @Column(nullable = false, precision = 18, scale = 6) private BigDecimal totalEqua;
    @Column(nullable = false, precision = 18, scale = 6) private BigDecimal feesEqua; // 0.1%

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

