package com.rayen.walletManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyTransaction(Long walletId, String type, Double amount, String details) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("walletId", walletId);
        notification.put("type", type);
        notification.put("amount", amount);
        notification.put("details", details);
        notification.put("timestamp", LocalDateTime.now().toString());

        messagingTemplate.convertAndSend("/topic/wallet/" + walletId, notification);
        messagingTemplate.convertAndSend("/topic/transactions", notification);
    }

    public void notifyFraudAlert(Long walletId, String riskLevel, String action) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("walletId", walletId);
        alert.put("type", "FRAUD_ALERT");
        alert.put("riskLevel", riskLevel);
        alert.put("action", action);
        alert.put("timestamp", LocalDateTime.now().toString());

        messagingTemplate.convertAndSend("/topic/fraud-alerts", alert);
        messagingTemplate.convertAndSend("/topic/wallet/" + walletId, alert);
    }

    public void notifyLoyaltyEvent(Long walletId, String event, Integer points) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("walletId", walletId);
        notification.put("type", "LOYALTY_EVENT");
        notification.put("event", event);
        notification.put("points", points);
        notification.put("timestamp", LocalDateTime.now().toString());

        messagingTemplate.convertAndSend("/topic/wallet/" + walletId, notification);
    }
}
