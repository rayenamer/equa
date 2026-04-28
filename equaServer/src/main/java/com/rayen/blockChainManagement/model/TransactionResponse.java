package com.rayen.blockChainManagement.model;

import com.rayen.blockChainManagement.entity.TransactionStatus;
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
public class TransactionResponse {
     private Integer transactionId;
     private String fromWallet;
     private String toWallet;
     private BigDecimal amount;
     private LocalDateTime timestamp;
     private TransactionStatus status; //enum
     private String transactionHash;
     private BigDecimal fee;
}
