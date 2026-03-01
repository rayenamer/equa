package com.rayen.walletManagement.controller;

import com.rayen.walletManagement.model.AssetDTO;
import com.rayen.walletManagement.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@Tag(name = "Asset Management", description = "CRUD & business operations for assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    // ==================== CRUD ====================

    @GetMapping
    @Operation(summary = "Get all assets")
    public ResponseEntity<List<AssetDTO>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get asset by ID")
    public ResponseEntity<AssetDTO> getAssetById(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getAssetById(id));
    }

    @GetMapping("/wallet/{walletId}")
    @Operation(summary = "Get assets by wallet ID")
    public ResponseEntity<List<AssetDTO>> getAssetsByWalletId(@PathVariable Long walletId) {
        return ResponseEntity.ok(assetService.getAssetsByWalletId(walletId));
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get assets by owner ID")
    public ResponseEntity<List<AssetDTO>> getAssetsByOwnerId(@PathVariable String ownerId) {
        return ResponseEntity.ok(assetService.getAssetsByOwnerId(ownerId));
    }

    @GetMapping("/type/{assetType}")
    @Operation(summary = "Get assets by type")
    public ResponseEntity<List<AssetDTO>> getAssetsByType(@PathVariable String assetType) {
        return ResponseEntity.ok(assetService.getAssetsByType(assetType));
    }

    // ==================== BUSINESS OPERATIONS ====================

    @PostMapping
    @Operation(summary = "Register a new asset (with collateral credit to wallet)")
    public ResponseEntity<AssetDTO> registerAsset(@RequestBody AssetDTO assetDTO) {
        return new ResponseEntity<>(assetService.registerAsset(assetDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/value")
    @Operation(summary = "Update asset value (with collateral adjustment)")
    public ResponseEntity<AssetDTO> updateAssetValue(@PathVariable Long id, @RequestParam Double newValue) {
        return ResponseEntity.ok(assetService.updateAssetValue(id, newValue));
    }

    @PutMapping("/{id}/transfer")
    @Operation(summary = "Transfer asset to a new owner (with collateral transfer)")
    public ResponseEntity<AssetDTO> transferAsset(@PathVariable Long id, @RequestParam String newOwnerId) {
        return ResponseEntity.ok(assetService.transferAsset(id, newOwnerId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete asset")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/wallet/{walletId}/total-value")
    @Operation(summary = "Get total asset value for a wallet")
    public ResponseEntity<Double> getTotalAssetValue(@PathVariable Long walletId) {
        return ResponseEntity.ok(assetService.getTotalAssetValue(walletId));
    }
}
