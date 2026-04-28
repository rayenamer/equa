package com.rayen.blockChainManagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    private Integer transactionId;
    private String fromWallet;
    private String toWallet;
    private BigDecimal amount;
    private LocalDateTime timestamp;

}
