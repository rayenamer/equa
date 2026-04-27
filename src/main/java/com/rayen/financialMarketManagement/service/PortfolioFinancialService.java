package com.rayen.financialMarketManagement.service;

import com.rayen.financialMarketManagement.dto.*;
import com.rayen.financialMarketManagement.entity.*;
import com.rayen.financialMarketManagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioFinancialService {

    private final HedgePortfolioFinancialRepository   portfolioRepo;
    private final HedgeTransactionFinancialRepository txRepo;

    public PortfolioResponseFinancial getPortfolio(Long userId) {
        HedgePortfolioFinancial portfolio = portfolioRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No portfolio found"));

        List<PortfolioItemResponseFinancial> items = portfolio.getItems().stream().map(i -> {
            BigDecimal current = i.getAsset().getCurrentPriceEqua();
            BigDecimal total   = current.multiply(i.getQuantity());
            BigDecimal pnl     = total.subtract(i.getAvgBuyPriceEqua().multiply(i.getQuantity()));
            return PortfolioItemResponseFinancial.builder()
                    .assetName(i.getAsset().getName()).ticker(i.getAsset().getTicker())
                    .quantity(i.getQuantity()).avgBuyPriceEqua(i.getAvgBuyPriceEqua())
                    .currentPriceEqua(current).totalValueEqua(total).pnlEqua(pnl)
                    .build();
        }).collect(Collectors.toList());

        BigDecimal totalValue = items.stream()
                .map(PortfolioItemResponseFinancial::getTotalValueEqua)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return PortfolioResponseFinancial.builder()
                .userId(userId).items(items).totalValueEqua(totalValue).build();
    }

    public List<TransactionResponseFinancial> getTransactions(Long userId) {
        return txRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(tx -> TransactionResponseFinancial.builder()
                        .id(tx.getId()).type(tx.getType()).ticker(tx.getAsset().getTicker())
                        .quantity(tx.getQuantity()).pricePerUnitEqua(tx.getPricePerUnitEqua())
                        .createdAt(tx.getCreatedAt()).build())
                .collect(Collectors.toList());
    }
}
