package com.rayen.financialMarketManagement.controller;

import com.rayen.financialMarketManagement.service.AssetCurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Slf4j
public class AssetCurrencyController {

    private final AssetCurrencyService assetCurrencyService;

    @GetMapping("/{id}/convert-to-dinar")
    public Float convertToEur(@PathVariable Integer id) {
        log.info("REST request to convert asset {} to EUR", id);
        return assetCurrencyService.convertToEur(id);
    }

    @GetMapping("/convert-all-to-dinar")
    public Map<Integer, Float> convertAllToEur() {
        log.info("REST request to convert all assets to EUR");
        return assetCurrencyService.convertAllToEur();
    }
}
