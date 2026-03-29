package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.model.RiskAssessmentDTO;
import com.rayen.walletManagement.repository.AssetRepository;
import com.rayen.walletManagement.repository.TokenRepository;
import com.rayen.walletManagement.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RiskAssessmentService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private AssetRepository assetRepository;

    // ==================== AI SCORING MODEL ====================

    /**
     * Computes a credit score using a weighted scoring model.
     * Factors: transaction history, balance stability, asset coverage, late payments.
     * This simulates an AI/ML scoring model with logistic regression-like prediction.
     */
    public RiskAssessmentDTO assessRisk(Long customerId) {
        Wallet wallet = walletRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for customer: " + customerId));

        Double totalAssetValue = assetRepository.getTotalAssetValueByWalletId(wallet.getWalletId());
        totalAssetValue = totalAssetValue != null ? totalAssetValue : 0.0;

        Double totalTokenValue = tokenRepository.getTotalConvertedValueByWalletId(wallet.getWalletId());
        totalTokenValue = totalTokenValue != null ? totalTokenValue : 0.0;

        int totalTransactions = wallet.getTransactionHistory() != null ? wallet.getTransactionHistory().size() : 0;

        // Count late payments (simulated from transaction history keywords)
        int latePayments = countLatePayments(wallet.getTransactionHistory());

        // Calculate financial indicators
        Map<String, Double> indicators = new HashMap<>();
        indicators.put("walletBalance", wallet.getBalance());
        indicators.put("totalAssetValue", totalAssetValue);
        indicators.put("totalTokenValue", totalTokenValue);
        indicators.put("totalTransactions", (double) totalTransactions);
        indicators.put("latePayments", (double) latePayments);

        // Asset-to-debt ratio
        Double assetCoverageRatio = wallet.getBalance() > 0
                ? totalAssetValue / wallet.getBalance()
                : 0.0;
        indicators.put("assetCoverageRatio", assetCoverageRatio);

        // Repayment rate
        Double repaymentRate = calculateRepaymentRate(wallet.getTransactionHistory());
        indicators.put("repaymentRate", repaymentRate);

        // ==================== SCORING ALGORITHM ====================
        // Weighted scoring model (simulating logistic regression)
        int creditScore = calculateCreditScore(
                wallet.getBalance(),
                totalAssetValue,
                totalTransactions,
                latePayments,
                repaymentRate,
                assetCoverageRatio
        );

        // Predict default probability using sigmoid function
        Double defaultProbability = predictDefaultProbability(
                creditScore, latePayments, repaymentRate, wallet.getBalance()
        );

        // Risk level classification
        String riskLevel = classifyRiskLevel(creditScore, defaultProbability);

        // Max allowed transaction based on risk
        Double maxAllowedTransaction = calculateMaxTransaction(creditScore, wallet.getBalance(), totalAssetValue);

        // Recommendation engine
        String recommendation = generateRecommendation(riskLevel, defaultProbability, creditScore);

        return RiskAssessmentDTO.builder()
                .customerId(customerId)
                .creditScore(creditScore)
                .riskLevel(riskLevel)
                .maxAllowedTransaction(maxAllowedTransaction)
                .totalAssetValue(totalAssetValue)
                .walletBalance(wallet.getBalance())
                .totalTransactions(totalTransactions)
                .latePayments(latePayments)
                .repaymentRate(repaymentRate)
                .predictedDefaultProbability(Math.round(defaultProbability * 10000.0) / 10000.0)
                .recommendation(recommendation)
                .financialIndicators(indicators)
                .assessmentDate(LocalDateTime.now())
                .build();
    }

    /**
     * Credit score calculation (300-850 range like FICO).
     * Weighted factors:
     *  - Balance stability: 25%
     *  - Asset coverage: 25%
     *  - Transaction history: 20%
     *  - Late payments: 20%
     *  - Repayment rate: 10%
     */
    private int calculateCreditScore(Double balance, Double assetValue, int transactions,
                                     int latePayments, Double repaymentRate, Double assetCoverageRatio) {
        double baseScore = 500.0;

        // Balance factor (0-100 points)
        double balanceFactor = Math.min(balance / 1000.0, 1.0) * 100;

        // Asset coverage factor (0-100 points)
        double assetFactor = Math.min(assetCoverageRatio, 2.0) * 50;

        // Transaction history factor (0-80 points) - more transactions = more data = better
        double transactionFactor = Math.min(transactions / 50.0, 1.0) * 80;

        // Late payment penalty (-200 to 0 points)
        double latePenalty = Math.min(latePayments * 40.0, 200.0);

        // Repayment rate bonus (0-70 points)
        double repaymentBonus = repaymentRate * 70;

        double score = baseScore + (balanceFactor * 0.25) + (assetFactor * 0.25)
                + (transactionFactor * 0.20) - (latePenalty * 0.20) + (repaymentBonus * 0.10);

        return (int) Math.max(300, Math.min(850, score));
    }

    /**
     * Sigmoid-based default probability prediction.
     * Simulates a logistic regression model output.
     */
    private Double predictDefaultProbability(int creditScore, int latePayments,
                                             Double repaymentRate, Double balance) {
        // Feature engineering
        double normalizedScore = (creditScore - 300.0) / 550.0; // 0 to 1
        double normalizedLate = Math.min(latePayments / 10.0, 1.0);
        double normalizedBalance = Math.min(balance / 10000.0, 1.0);

        // Logistic regression coefficients (simulated trained weights)
        double w0 = 2.0;   // bias
        double w1 = -3.5;  // credit score weight (negative = lower score -> higher default)
        double w2 = 2.0;   // late payments weight
        double w3 = -1.5;  // repayment rate weight
        double w4 = -1.0;  // balance weight

        double z = w0 + (w1 * normalizedScore) + (w2 * normalizedLate)
                + (w3 * repaymentRate) + (w4 * normalizedBalance);

        // Sigmoid function
        return 1.0 / (1.0 + Math.exp(-z));
    }

    /**
     * Risk level classification based on credit score and default probability.
     */
    private String classifyRiskLevel(int creditScore, Double defaultProbability) {
        if (creditScore >= 750 && defaultProbability < 0.1) return "LOW";
        if (creditScore >= 600 && defaultProbability < 0.3) return "MEDIUM";
        if (creditScore >= 450 && defaultProbability < 0.6) return "HIGH";
        return "CRITICAL";
    }

    /**
     * Calculate maximum allowed transaction based on risk profile.
     */
    private Double calculateMaxTransaction(int creditScore, Double balance, Double assetValue) {
        double riskMultiplier;
        if (creditScore >= 750) riskMultiplier = 2.0;
        else if (creditScore >= 600) riskMultiplier = 1.0;
        else if (creditScore >= 450) riskMultiplier = 0.5;
        else riskMultiplier = 0.2;

        return (balance + assetValue * 0.1) * riskMultiplier;
    }

    /**
     * Generate human-readable recommendation.
     */
    private String generateRecommendation(String riskLevel, Double defaultProbability, int creditScore) {
        return switch (riskLevel) {
            case "LOW" -> "APPROVE - Excellent financial profile. Score: " + creditScore
                    + ". Default probability: " + String.format("%.1f%%", defaultProbability * 100);
            case "MEDIUM" -> "REVIEW - Moderate risk. Score: " + creditScore
                    + ". Consider additional verification before large transactions.";
            case "HIGH" -> "DENY - High risk profile. Score: " + creditScore
                    + ". Default probability: " + String.format("%.1f%%", defaultProbability * 100)
                    + ". Recommend asset collateral or guarantor.";
            case "CRITICAL" -> "DENY - Critical risk. Score: " + creditScore
                    + ". Account may need suspension and review.";
            default -> "REVIEW - Unable to fully assess risk.";
        };
    }

    private int countLatePayments(List<String> transactionHistory) {
        if (transactionHistory == null) return 0;
        return (int) transactionHistory.stream()
                .filter(t -> t.contains("LATE") || t.contains("PENALTY") || t.contains("OVERDUE"))
                .count();
    }

    private Double calculateRepaymentRate(List<String> transactionHistory) {
        if (transactionHistory == null || transactionHistory.isEmpty()) return 1.0;
        long totalLoans = transactionHistory.stream()
                .filter(t -> t.contains("LOAN") || t.contains("RECEIVE"))
                .count();
        long repayments = transactionHistory.stream()
                .filter(t -> t.contains("REPAY") || t.contains("SEND"))
                .count();
        if (totalLoans == 0) return 1.0;
        return Math.min((double) repayments / totalLoans, 1.0);
    }
}
