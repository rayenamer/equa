package com.rayen.walletManagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void notifyTransaction(Long walletId, String type, Double amount, String details) {
        log.info("[NOTIFICATION] Wallet {} | {} | {} | {}", walletId, type, amount, details);
    }

    public void notifyFraudAlert(Long walletId, String riskLevel, String action) {
        log.warn("[FRAUD ALERT] Wallet {} | Risk: {} | Action: {}", walletId, riskLevel, action);
    }

    public void notifyLoyaltyEvent(Long walletId, String event, Integer points) {
        log.info("[LOYALTY] Wallet {} | {} | +{} points", walletId, event, points);
    }
}
