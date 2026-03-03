package com.rayen.financialMarketManagement.service;

import com.rayen.financialMarketManagement.entity.Asset;
import com.rayen.financialMarketManagement.entity.AssetType;
import com.rayen.financialMarketManagement.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetService {

    private final AssetRepository assetRepository;

    @Transactional
    public Asset createAsset(Asset asset) {
        log.info("Creating new asset of type: {}", asset.getAssetType());
        return assetRepository.save(asset);
    }

    @Transactional(readOnly = true)
    public Asset getAssetById(Integer assetId) {
        return assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with ID: " + assetId));
    }

    @Transactional(readOnly = true)
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Asset> getAssetsByOwnerId(String ownerId) {
        return assetRepository.findByOwnerId(ownerId);
    }

    @Transactional(readOnly = true)
    public List<Asset> getAssetsByType(AssetType assetType) {
        return assetRepository.findByAssetType(assetType);
    }

    @Transactional
    public Asset updateAsset(Integer assetId, Asset updatedAsset) {
        Asset existing = assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with ID: " + assetId));

        existing.setOwnerId(updatedAsset.getOwnerId());
        existing.setAssetType(updatedAsset.getAssetType());
        existing.setDemand(updatedAsset.getDemand());
        existing.setQuantity(updatedAsset.getQuantity());
        existing.setTotalBought(updatedAsset.getTotalBought());
        existing.setTotalSold(updatedAsset.getTotalSold());

        log.info("Updating asset with ID: {}", assetId);
        return assetRepository.save(existing);
    }

    @Transactional
    public void deleteAsset(Integer assetId) {
        if (!assetRepository.existsById(assetId)) {
            throw new IllegalArgumentException("Asset not found with ID: " + assetId);
        }
        log.info("Deleting asset with ID: {}", assetId);
        assetRepository.deleteById(assetId);
    }
}
