package com.rayen.financialMarketManagement.service;

import com.rayen.financialMarketManagement.dto.*;
import com.rayen.financialMarketManagement.entity.*;
import com.rayen.financialMarketManagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class TradeFinancialService {

    private static final BigDecimal FEE_RATE          = new BigDecimal("0.001");
    private static final BigDecimal PRICE_SENSITIVITY = new BigDecimal("0.5");

    private static final BigDecimal PRICE_SENSITIVITY_MIN = new BigDecimal("0.01");
    private static final BigDecimal PRICE_SENSITIVITY_MAX = new BigDecimal("1.00");

    private final AssetFinancialRepository              assetRepo;
    private final HedgePortfolioFinancialRepository     portfolioRepo;
    private final HedgePortfolioItemFinancialRepository itemRepo;
    private final HedgeTransactionFinancialRepository   txRepo;
    private final AssetFinancialService                 assetService;

    @Transactional
    public TradeResponseFinancial buy(TradeRequestFinancial req, Long userId) {
        AssetFinancial asset = assetService.findAsset(req.getAssetId());
        BigDecimal price     = asset.getCurrentPriceEqua();

        BigDecimal spendEqua = req.getAmountEqua();
        BigDecimal fees      = spendEqua.multiply(FEE_RATE).setScale(6, RoundingMode.HALF_UP);
        BigDecimal subtotal  = spendEqua.subtract(fees);
        BigDecimal qty       = subtotal.divide(price, 6, RoundingMode.HALF_UP);

        // TODO: deduct `spendEqua` EQUA from wallet → walletService.debit(userId, spendEqua)

        BigDecimal impact   = subtotal.divide(marketCap(asset), 6, RoundingMode.HALF_UP).multiply(PRICE_SENSITIVITY);
        BigDecimal newPrice = price.multiply(BigDecimal.ONE.add(impact)).setScale(6, RoundingMode.HALF_UP);
        asset.setCurrentPriceEqua(newPrice);
        asset.setVolume24h(asset.getVolume24h().add(subtotal));
        assetRepo.save(asset);
        assetService.recordPrice(asset);

        HedgePortfolioFinancial portfolio = portfolioRepo.findByUserId(userId)
                .orElseGet(() -> portfolioRepo.save(HedgePortfolioFinancial.builder().userId(userId).build()));

        HedgePortfolioItemFinancial item = itemRepo.findByPortfolioIdAndAssetId(portfolio.getId(), asset.getId())
                .orElseGet(() -> HedgePortfolioItemFinancial.builder()
                        .portfolio(portfolio).asset(asset)
                        .quantity(BigDecimal.ZERO).avgBuyPriceEqua(BigDecimal.ZERO).build());

        BigDecimal oldTotal = item.getQuantity().multiply(item.getAvgBuyPriceEqua());
        BigDecimal newQty   = item.getQuantity().add(qty);
        item.setAvgBuyPriceEqua(oldTotal.add(subtotal).divide(newQty, 6, RoundingMode.HALF_UP));
        item.setQuantity(newQty);
        itemRepo.save(item);

        HedgeTransactionFinancial tx = HedgeTransactionFinancial.builder()
                .userId(userId).asset(asset).type(TradeType.BUY)
                .quantity(qty).pricePerUnitEqua(price)
                .totalEqua(spendEqua).feesEqua(fees).build();
        txRepo.save(tx);
        return toTradeResponse(tx);
    }

    @Transactional
    public TradeResponseFinancial sell(TradeRequestFinancial req, Long userId) {
        AssetFinancial asset = assetService.findAsset(req.getAssetId());
        BigDecimal price     = asset.getCurrentPriceEqua();

        BigDecimal sellEqua = req.getAmountEqua();
        BigDecimal qty      = sellEqua.divide(price, 6, RoundingMode.HALF_UP);
        BigDecimal subtotal = qty.multiply(price);
        BigDecimal fees     = subtotal.multiply(FEE_RATE).setScale(6, RoundingMode.HALF_UP);
        BigDecimal received = subtotal.subtract(fees);

        HedgePortfolioFinancial portfolio = portfolioRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No portfolio found"));
        HedgePortfolioItemFinancial item = itemRepo.findByPortfolioIdAndAssetId(portfolio.getId(), asset.getId())
                .orElseThrow(() -> new RuntimeException("Asset not in portfolio"));

        if (item.getQuantity().compareTo(qty) < 0)
            throw new RuntimeException("Insufficient holdings");

        // TODO: credit `received` EQUA to wallet → walletService.credit(userId, received)

        BigDecimal impact   = subtotal.divide(marketCap(asset), 6, RoundingMode.HALF_UP).multiply(PRICE_SENSITIVITY);
        BigDecimal newPrice = price.multiply(BigDecimal.ONE.subtract(impact)).setScale(6, RoundingMode.HALF_UP);
        asset.setCurrentPriceEqua(newPrice);
        asset.setVolume24h(asset.getVolume24h().add(subtotal));
        assetRepo.save(asset);
        assetService.recordPrice(asset);

        BigDecimal newQty = item.getQuantity().subtract(qty);
        if (newQty.compareTo(BigDecimal.ZERO) == 0) itemRepo.delete(item);
        else { item.setQuantity(newQty); itemRepo.save(item); }

        HedgeTransactionFinancial tx = HedgeTransactionFinancial.builder()
                .userId(userId).asset(asset).type(TradeType.SELL)
                .quantity(qty).pricePerUnitEqua(price)
                .totalEqua(received).feesEqua(fees).build();
        txRepo.save(tx);
        return toTradeResponse(tx);
    }

    private BigDecimal marketCap(AssetFinancial a) {
        BigDecimal mc = a.getCurrentPriceEqua().multiply(a.getCirculatingSupply());
        return mc.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : mc;
    }

    private TradeResponseFinancial toTradeResponse(HedgeTransactionFinancial tx) {
        return TradeResponseFinancial.builder()
                .transactionId(tx.getId()).type(tx.getType())
                .ticker(tx.getAsset().getTicker()).quantity(tx.getQuantity())
                .pricePerUnitEqua(tx.getPricePerUnitEqua())
                .totalEqua(tx.getTotalEqua()).feesEqua(tx.getFeesEqua())
                .createdAt(tx.getCreatedAt()).build();
    }


}