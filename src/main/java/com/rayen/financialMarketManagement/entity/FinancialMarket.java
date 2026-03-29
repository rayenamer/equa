package com.rayen.financialMarketManagement.entity;

import com.rayen.blockChainManagement.entity.Transaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "financial_markets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialMarket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "market_id")
    private Integer marketId;

    @Column(name = "market_name", nullable = false)
    private String marketName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id")
    private List<Asset> availableAssets = new ArrayList<>();


    @Column(name = "exchange_rate", nullable = false)
    private Float exchangeRate;
}
