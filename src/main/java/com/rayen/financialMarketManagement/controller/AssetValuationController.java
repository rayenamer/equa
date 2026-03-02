package com.rayen.financialMarketManagement.controller;

import com.rayen.financialMarketManagement.entity.Asset;
import com.rayen.financialMarketManagement.service.AssetValuationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetValuationController {

    private final AssetValuationService assetValuationService;

    @PutMapping("/{id}/calculate")
    public Asset calculateAssetValue(@PathVariable Integer id) {
        return assetValuationService.calculateAndUpdateValue(id);
    }

    @PutMapping("/recalculate-all")
    public List<Asset> recalculateAllAssets() {
        return assetValuationService.recalculateAllAssets();
    }
}
