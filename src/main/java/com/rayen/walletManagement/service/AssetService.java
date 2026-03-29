package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.Asset;
import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.model.AssetDTO;
import com.rayen.walletManagement.repository.AssetRepository;
import com.rayen.walletManagement.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private WalletRepository walletRepository;

    private static final Double MAX_ASSET_VALUE = 10000000.0;

    // ==================== CRUD ====================

    public List<AssetDTO> getAllAssets() {
        return assetRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AssetDTO getAssetById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + id));
        return convertToDTO(asset);
    }

    public List<AssetDTO> getAssetsByWalletId(Long walletId) {
        return assetRepository.findByWalletWalletId(walletId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AssetDTO> getAssetsByOwnerId(String ownerId) {
        return assetRepository.findByOwnerId(ownerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AssetDTO> getAssetsByType(String assetType) {
        return assetRepository.findByAssetType(assetType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ==================== BUSINESS RULES ====================

    @Transactional
    public AssetDTO registerAsset(AssetDTO assetDTO) {
        Wallet wallet = walletRepository.findById(assetDTO.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + assetDTO.getWalletId()));

        if (!"ACTIVE".equals(wallet.getStatus())) {
            throw new RuntimeException("Cannot register asset for inactive wallet");
        }

        if (assetDTO.getValue() <= 0 || assetDTO.getValue() > MAX_ASSET_VALUE) {
            throw new RuntimeException("Asset value must be between 0 and " + MAX_ASSET_VALUE);
        }

        validateAssetType(assetDTO.getAssetType());

        Asset asset = new Asset();
        asset.setOwnerId(assetDTO.getOwnerId());
        asset.setAssetType(assetDTO.getAssetType());
        asset.setValue(assetDTO.getValue());
        asset.setStatus("REGISTERED");
        asset.setWallet(wallet);

        // Credit wallet balance with a fraction of asset value (collateral)
        Double collateralValue = assetDTO.getValue() * 0.1; // 10% of asset value
        wallet.receiveTokens(collateralValue);
        wallet.getTransactionHistory().add(
                java.time.LocalDateTime.now() + " | ASSET_REGISTERED | collateral: +" + collateralValue
                        + " | asset: " + assetDTO.getAssetType() + " valued at " + assetDTO.getValue()
        );

        walletRepository.save(wallet);
        Asset saved = assetRepository.save(asset);
        return convertToDTO(saved);
    }

    @Transactional
    public AssetDTO updateAssetValue(Long assetId, Double newValue) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + assetId));

        if (newValue <= 0 || newValue > MAX_ASSET_VALUE) {
            throw new RuntimeException("Asset value must be between 0 and " + MAX_ASSET_VALUE);
        }

        Double oldValue = asset.getValue();
        asset.updateAssetValue(newValue);

        // Adjust wallet balance based on value change (collateral adjustment)
        Wallet wallet = asset.getWallet();
        Double collateralDiff = (newValue - oldValue) * 0.1;
        if (collateralDiff > 0) {
            wallet.receiveTokens(collateralDiff);
        } else if (collateralDiff < 0 && wallet.getBalance() >= Math.abs(collateralDiff)) {
            wallet.withdraw(Math.abs(collateralDiff));
        }

        wallet.getTransactionHistory().add(
                java.time.LocalDateTime.now() + " | ASSET_REVALUED | " + oldValue + " -> " + newValue
                        + " | collateral adj: " + collateralDiff
        );

        walletRepository.save(wallet);
        Asset saved = assetRepository.save(asset);
        return convertToDTO(saved);
    }

    @Transactional
    public AssetDTO transferAsset(Long assetId, String newOwnerId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + assetId));

        if (newOwnerId == null || newOwnerId.isBlank()) {
            throw new RuntimeException("New owner ID is required");
        }

        Wallet currentWallet = asset.getWallet();
        Wallet newOwnerWallet = walletRepository.findByCustomerId(Long.parseLong(newOwnerId))
                .orElseThrow(() -> new RuntimeException("New owner wallet not found"));

        if (!"ACTIVE".equals(newOwnerWallet.getStatus())) {
            throw new RuntimeException("New owner's wallet is not active");
        }

        // Remove collateral from current owner
        Double collateral = asset.getValue() * 0.1;
        if (currentWallet.getBalance() >= collateral) {
            currentWallet.withdraw(collateral);
        }

        // Transfer asset
        asset.transferAsset(newOwnerId);
        asset.setWallet(newOwnerWallet);

        // Add collateral to new owner
        newOwnerWallet.receiveTokens(collateral);

        currentWallet.getTransactionHistory().add(
                java.time.LocalDateTime.now() + " | ASSET_TRANSFERRED_OUT | asset: " + assetId + " | to: " + newOwnerId
        );
        newOwnerWallet.getTransactionHistory().add(
                java.time.LocalDateTime.now() + " | ASSET_TRANSFERRED_IN | asset: " + assetId + " | from: " + asset.getOwnerId()
        );

        walletRepository.save(currentWallet);
        walletRepository.save(newOwnerWallet);
        Asset saved = assetRepository.save(asset);
        return convertToDTO(saved);
    }

    public void deleteAsset(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + id));
        assetRepository.delete(asset);
    }

    public Double getTotalAssetValue(Long walletId) {
        Double total = assetRepository.getTotalAssetValueByWalletId(walletId);
        return total != null ? total : 0.0;
    }

    // ==================== HELPERS ====================

    private void validateAssetType(String assetType) {
        List<String> validTypes = List.of("REAL_ESTATE", "VEHICLE", "EQUIPMENT", "DIGITAL", "OTHER");
        if (!validTypes.contains(assetType)) {
            throw new RuntimeException("Invalid asset type. Valid types: " + validTypes);
        }
    }

    // ==================== CONVERTERS ====================

    private AssetDTO convertToDTO(Asset asset) {
        return AssetDTO.builder()
                .assetId(asset.getAssetId())
                .ownerId(asset.getOwnerId())
                .assetType(asset.getAssetType())
                .value(asset.getValue())
                .status(asset.getStatus())
                .walletId(asset.getWallet() != null ? asset.getWallet().getWalletId() : null)
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }
}
