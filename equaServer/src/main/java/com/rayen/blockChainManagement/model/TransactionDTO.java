package com.rayen.blockChainManagement.model;

import com.rayen.blockChainManagement.entity.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(
        Integer transactionId,
        String fromWallet,
        String toWallet,
        BigDecimal amount,
        LocalDateTime timestamp,
        TransactionStatus status,
        String transactionHash,
        BigDecimal fee
) {}
