package com.rayen.walletManagement.service;

import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.model.CurrencyCode;
import com.rayen.walletManagement.model.FraudAssessment;
import com.rayen.walletManagement.model.FraudRiskLevel;
import com.rayen.walletManagement.model.WalletOperationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
public class FraudDetectionService {

    private static final BigDecimal HIGH_AMOUNT_THRESHOLD = BigDecimal.valueOf(10_000);
    private static final BigDecimal CRITICAL_AMOUNT_THRESHOLD = BigDecimal.valueOf(50_000);

    public FraudAssessment assess(Wallet wallet, WalletOperationType operationType, CurrencyCode currency, BigDecimal amount) {
        BigDecimal riskScore = BigDecimal.ZERO;
        LocalDateTime now = LocalDateTime.now();

        if (wallet.getLastActivityAt() != null) {
            Duration sinceLast = Duration.between(wallet.getLastActivityAt(), now);
            if (sinceLast.toHours() < 1 && wallet.getRecentBalanceChanges() != null && wallet.getRecentBalanceChanges() > 4) {
                riskScore = riskScore.add(BigDecimal.valueOf(25));
            }
            if (sinceLast.toDays() > 30) {
                riskScore = riskScore.add(BigDecimal.valueOf(20));
            }
        }

        if (amount != null) {
            if (amount.compareTo(HIGH_AMOUNT_THRESHOLD) >= 0) {
                riskScore = riskScore.add(BigDecimal.valueOf(20));
            }
            if (amount.compareTo(CRITICAL_AMOUNT_THRESHOLD) >= 0) {
                riskScore = riskScore.add(BigDecimal.valueOf(25));
            }
        }

        if (operationType == WalletOperationType.CONVERSION) {
            riskScore = riskScore.add(BigDecimal.valueOf(15));
        }

        if (wallet.getRecentConversionCount() != null && wallet.getRecentConversionCount() > 3) {
            riskScore = riskScore.add(BigDecimal.valueOf(20));
        }

        if (currency == CurrencyCode.BTC && amount != null && amount.compareTo(BigDecimal.valueOf(1)) >= 0) {
            riskScore = riskScore.add(BigDecimal.valueOf(15));
        }

        FraudRiskLevel level = mapScoreToLevel(riskScore);
        wallet.setFraudRiskLevel(level);
        wallet.setLastActivityAt(now);

        if (amount != null && operationType != WalletOperationType.LOYALTY_REDEEM) {
            wallet.setLastBalanceChangeAt(now);
            wallet.setRecentBalanceChanges((wallet.getRecentBalanceChanges() == null ? 0 : wallet.getRecentBalanceChanges()) + 1);
        }

        if (operationType == WalletOperationType.CONVERSION) {
            wallet.setLastConversionAt(now);
            wallet.setRecentConversionCount((wallet.getRecentConversionCount() == null ? 0 : wallet.getRecentConversionCount()) + 1);
        }

        if (level == FraudRiskLevel.CRITICAL) {
            wallet.setStatus("SUSPENDED");
            log.warn("Wallet {} flagged as CRITICAL fraud risk", wallet.getWalletId());
        }

        return new FraudAssessment(riskScore.min(BigDecimal.valueOf(100)), level);
    }

    private FraudRiskLevel mapScoreToLevel(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return FraudRiskLevel.CRITICAL;
        }
        if (score.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return FraudRiskLevel.HIGH;
        }
        if (score.compareTo(BigDecimal.valueOf(30)) >= 0) {
            return FraudRiskLevel.MEDIUM;
        }
        return FraudRiskLevel.LOW;
    }
}
