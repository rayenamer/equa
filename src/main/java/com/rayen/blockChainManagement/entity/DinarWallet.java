package com.rayen.blockChainManagement.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DinarWallet {
    private String walletId;
    private String userId;
    private BigDecimal balance;
    private DinarWalletStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
