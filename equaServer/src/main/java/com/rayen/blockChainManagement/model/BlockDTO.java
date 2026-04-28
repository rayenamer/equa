package com.rayen.blockChainManagement.model;


import java.time.LocalDateTime;
import java.util.List;

public record BlockDTO(
        Integer blockId,
        String previousHash,
        String blockHash,
        LocalDateTime timestamp,
        Long blockSize,
        Integer previousBlockId,        // only the ID, NOT the full Block object
        List<TransactionDTO> transactions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
