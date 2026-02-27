package com.rayen.blockChainManagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockResponse{

    private Integer blockId;
    private String previousHash;
    private String blockHash;
    private LocalDateTime timestamp;
    private Long blockSize;

    // Navigation info
    private Integer previousBlockId;
    private Integer nextBlockId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
