package com.rayen.walletManagement.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {

    private Double amount;
    private String recipientPublicKey;
    private Long recipientWalletId;
}
