package com.rayen.financialMarketManagement.service;

import com.rayen.financialMarketManagement.dto.*;
import com.rayen.financialMarketManagement.entity.*;
import com.rayen.financialMarketManagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetFinancialService {

    private final AssetFinancialRepository assetRepo;
    private final AssetPriceHistoryFinancialRepository priceHistoryRepo;

    public List<AssetResponseFinancial> getAll() {
        return assetRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AssetResponseFinancial getById(Long id) {
        return toResponse(findAsset(id));
    }

    @Transactional
    public AssetResponseFinancial create(AssetRequestFinancial req, Long userId) {
        AssetFinancial asset = AssetFinancial.builder()
                .name(req.getName())
                .ticker(req.getTicker().toUpperCase())
                .category(req.getCategory())
                .description(req.getDescription())
                .logoUrl(req.getLogoUrl())
                .currentPriceEqua(req.getInitialPriceEqua())
                .totalSupply(req.getTotalSupply())
                .circulatingSupply(req.getTotalSupply())
                .creatorUserId(userId)
                .status(AssetStatus.PENDING)
                .volume24h(req.getVolume24h())
                .build();
        asset = assetRepo.save(asset);
        recordPrice(asset);
        return toResponse(asset);
    }

    public List<PriceHistoryResponseFinancial> getPriceHistory(Long assetId) {
        return priceHistoryRepo.findByAssetIdOrderByRecordedAtAsc(assetId).stream()
                .map(h -> new PriceHistoryResponseFinancial(h.getPriceEqua(), h.getRecordedAt()))
                .collect(Collectors.toList());
    }
    public List<PriceHistoryResponseFinancial> getAggregatedPriceHistory() {
        List<AssetPriceHistoryFinancial> all = priceHistoryRepo.findAllByOrderByRecordedAtAsc();

        // Group into 10-minute buckets using recordedAt
        Map<Long, List<AssetPriceHistoryFinancial>> groupedBy10Min = all.stream()
                .collect(Collectors.groupingBy(ph ->
                        ph.getRecordedAt()
                                .toEpochSecond(ZoneOffset.UTC) / 600  // 600s = 10 min
                ));

        return groupedBy10Min.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    List<AssetPriceHistoryFinancial> group = entry.getValue();

                    // Sum all priceEqua in the 10-min window
                    BigDecimal totalPrice = group.stream()
                            .map(AssetPriceHistoryFinancial::getPriceEqua)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Average recordedAt
                    long avgEpoch = (long) group.stream()
                            .mapToLong(ph -> ph.getRecordedAt().toEpochSecond(ZoneOffset.UTC))
                            .average()
                            .orElse(0);
                    LocalDateTime avgTime = LocalDateTime.ofEpochSecond(avgEpoch, 0, ZoneOffset.UTC);

                    return PriceHistoryResponseFinancial.builder()
                            .priceEqua(totalPrice)
                            .recordedAt(avgTime)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void recordPrice(AssetFinancial asset) {
        priceHistoryRepo.save(AssetPriceHistoryFinancial.builder()
                .asset(asset)
                .priceEqua(asset.getCurrentPriceEqua())
                .recordedAt(LocalDateTime.now())
                .build());
    }

    public AssetFinancial findAsset(Long id) {
        return assetRepo.findById(id).orElseThrow(() -> new RuntimeException("Asset not found: " + id));
    }

    private AssetResponseFinancial toResponse(AssetFinancial a) {
        return AssetResponseFinancial.builder()
                .id(a.getId()).name(a.getName()).ticker(a.getTicker())
                .category(a.getCategory()).description(a.getDescription())
                .logoUrl(a.getLogoUrl()).currentPriceEqua(a.getCurrentPriceEqua())
                .totalSupply(a.getTotalSupply()).circulatingSupply(a.getCirculatingSupply())
                .volume24h(a.getVolume24h()).status(a.getStatus())
                .verified(a.isVerified()).createdAt(a.getCreatedAt())
                .build();
    }

    public AssetMarketSummaryResponse getMarketSummary() {
        List<AssetFinancial> assets = assetRepo.findAll();

        BigDecimal totalMarketCap = assets.stream()
                .map(a -> a.getCurrentPriceEqua().multiply(a.getCirculatingSupply()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVolume24h = assets.stream()
                .map(AssetFinancial::getVolume24h)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSupply = assets.stream()
                .map(AssetFinancial::getTotalSupply)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCirculating = assets.stream()
                .map(AssetFinancial::getCirculatingSupply)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AssetMarketSummaryResponse.builder()
                .totalMarketCap(totalMarketCap)
                .totalVolume24h(totalVolume24h)
                .totalSupply(totalSupply)
                .totalCirculating(totalCirculating)
                .assetCount(assets.size())
                .build();
    }
}
