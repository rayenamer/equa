package com.rayen.financialMarketManagement.service;

import com.rayen.financialMarketManagement.entity.Asset;
import com.rayen.financialMarketManagement.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetCurrencyService {

    private final AssetRepository assetRepository;
    private static final float EQUA_TO_EUR = 105f;

    public Float convertToEur(Integer assetId) {
        log.info("Converting asset {} to EUR using conversion rate {}", assetId, EQUA_TO_EUR);
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with ID: " + assetId));
        
        float value = asset.getValue() != null ? asset.getValue() : 0f;
        return value * EQUA_TO_EUR;
    }

    public Map<Integer, Float> convertAllToEur() {
        log.info("Converting all assets to EUR using conversion rate {}", EQUA_TO_EUR);
        List<Asset> assets = assetRepository.findAll();
        
        return assets.stream()
                .collect(Collectors.toMap(
                        Asset::getAssetId,
                        asset -> {
                            float value = asset.getValue() != null ? asset.getValue() : 0f;
                            return value * EQUA_TO_EUR;
                        }
                ));
    }
}
