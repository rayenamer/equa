package com.rayen.walletManagement.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletDTO {

    private Long walletId;
    private Double balance;
    private Long customerId;
    private String publicKey;
    private String status;
    private String currency;
    private Integer loyaltyPoints;
    private String loyaltyTier;
    private List<String> transactionHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TokenDTO> tokens;
    private List<AssetDTO> assets;
}
