package com.rayen.financialMarketManagement.service;

import com.rayen.financialMarketManagement.entity.Asset;
import com.rayen.financialMarketManagement.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetValuationService {

    private final AssetRepository assetRepository;

    @Transactional
    public Asset calculateAndUpdateValue(Integer assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with ID: " + assetId));

        float finalValue = calculateValue(asset);
        asset.setValue(finalValue);

        log.info("Calculated value for asset {}: {}", assetId, finalValue);
        return assetRepository.save(asset);
    }

    @Transactional
    public List<Asset> recalculateAllAssets() {
        List<Asset> assets = assetRepository.findAll();

        for (Asset asset : assets) {
            float finalValue = calculateValue(asset);
            asset.setValue(finalValue);
            log.info("Recalculated value for asset {}: {}", asset.getAssetId(), finalValue);
        }

        return assetRepository.saveAll(assets);
    }

    private float calculateValue(Asset asset) {
        int demand = asset.getDemand() != null ? asset.getDemand() : 0;
        int quantity = asset.getQuantity() != null ? asset.getQuantity() : 0;
        int totalBought = asset.getTotalBought() != null ? asset.getTotalBought() : 0;
        int totalSold = asset.getTotalSold() != null ? asset.getTotalSold() : 0;

        float initialValue = asset.getValue() != null ? asset.getValue() : 0f;

        float baseValue = initialValue + (demand * 0.4f) + (totalBought * 0.4f) - (totalSold * 0.3f) - (quantity * 0.3f);

        float assetTypeFactor = switch (asset.getAssetType()) {
            case REAL_ESTATE -> 1.5f;
            case EQUITY -> 1.2f;
            case COMMODITY -> 1.0f;
        };

        return baseValue * assetTypeFactor;
    }
}
