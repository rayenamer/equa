package com.rayen.financialMarketManagement.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayen.financialMarketManagement.entity.AssetFinancial;
import com.rayen.financialMarketManagement.repository.AssetFinancialRepository;
import com.rayen.financialMarketManagement.repository.AssetPriceHistoryFinancialRepository;
import com.rayen.financialMarketManagement.repository.HedgePortfolioFinancialRepository;
import com.rayen.blockChainManagement.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FinancialMarketAnalysisService {

    private final AssetFinancialRepository assetFinancialRepository;
    private final AssetPriceHistoryFinancialRepository assetPriceHistoryFinancialRepository;
    private final HedgePortfolioFinancialRepository hedgePortfolioFinancialRepository;
    private final GeminiService geminiService;

    public Map<String, Object> analyzeMarket() {
        List<AssetFinancial> assets = assetFinancialRepository.findAll();
        long totalHedgePortfolios = hedgePortfolioFinancialRepository.count();

        StringBuilder prompt = new StringBuilder();
        prompt.append(
                "You are a seasoned financial market analyst for the EQUA ecosystem. Analyze the current state of our virtual financial market and give concise, professional, and insightful investment advice.\n\n");
        prompt.append("=== LATEST MARKET OVERVIEW ===\n");
        prompt.append("Total active assets: ").append(assets.size()).append("\n");
        prompt.append("Total user hedge portfolios: ").append(totalHedgePortfolios).append("\n\n");

        prompt.append("=== ASSETS DATA ===\n");
        assets.forEach(asset -> prompt.append(String.format(
                "Asset: %s (%s) | Category: %s | Status: %s | Current Price: %.4f EQUA | Circulating Supply: %d\n",
                asset.getName(), asset.getTicker(), asset.getCategory(), asset.getStatus(),
                asset.getCurrentPriceEqua(), asset.getCirculatingSupply())));

        prompt.append(
                "\nBased on this real-time data, you MUST return the response strictly as a single JSON object. Do not include any markdown blocks like ```json. Your JSON output must contain exactly these 5 keys:\n");
        prompt.append(
                "- \"insight\": (string) Your complete text advice and market overview, formatted nicely. Maximum 4 sentences.\n");
        prompt.append(
                "- \"marketHealthScore\": (number) An integer from 0 to 100 representing the overall market health based on the data.\n");
        prompt.append("- \"marketSentiment\": (string) The overall sentiment (e.g., Bullish, Bearish, Neutral).\n");
        prompt.append("- \"riskLevel\": (string) The risk level of the current market (e.g., Low, Moderate, High).\n");
        prompt.append(
                "- \"topAssetPick\": (string) The ticker of the most promising asset, or 'N/A' if none stand out.\n");

        String geminiResponse = geminiService.analyzeBlockchain(prompt.toString());

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(geminiResponse.replace("```json", "").replace("```", "").trim(),
                    new TypeReference<Map<String, Object>>() {
                    });
        } catch (Exception e) {
            return Map.of(
                    "insight", "Failed to parse AI response. Raw output: " + geminiResponse,
                    "marketHealthScore", 0,
                    "marketSentiment", "Unknown",
                    "riskLevel", "Unknown",
                    "topAssetPick", "None");
        }
    }
}
