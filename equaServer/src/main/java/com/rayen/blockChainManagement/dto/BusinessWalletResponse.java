package com.rayen.blockChainManagement.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BusinessWalletResponse {
    private Long walletId;
    private Long businessId;
    private String status;
    private Float equaAmount;
    private LocalDateTime lastActivityAt;
}
