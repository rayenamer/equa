package com.rayen.financialMarketManagement.controller;

import com.rayen.financialMarketManagement.entity.Asset;
import com.rayen.financialMarketManagement.entity.AssetType;
import com.rayen.financialMarketManagement.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
@Slf4j
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        log.info("REST request to create asset");
        Asset created = assetService.createAsset(asset);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable Integer id) {
        log.info("REST request to get asset by ID: {}", id);
        Asset asset = assetService.getAssetById(id);
        return ResponseEntity.ok(asset);
    }

    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets() {
        log.info("REST request to get all assets");
        List<Asset> assets = assetService.getAllAssets();
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Asset>> getAssetsByOwner(@PathVariable String ownerId) {
        log.info("REST request to get assets by owner: {}", ownerId);
        List<Asset> assets = assetService.getAssetsByOwnerId(ownerId);
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/type/{assetType}")
    public ResponseEntity<List<Asset>> getAssetsByType(@PathVariable AssetType assetType) {
        log.info("REST request to get assets by type: {}", assetType);
        List<Asset> assets = assetService.getAssetsByType(assetType);
        return ResponseEntity.ok(assets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(@PathVariable Integer id, @RequestBody Asset asset) {
        log.info("REST request to update asset with ID: {}", id);
        Asset updated = assetService.updateAsset(id, asset);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Integer id) {
        log.info("REST request to delete asset with ID: {}", id);
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }
}
