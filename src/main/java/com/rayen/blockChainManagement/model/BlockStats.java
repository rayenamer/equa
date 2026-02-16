package com.rayen.blockChainManagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockStats {
    private Long totalBlocks;
    private Double averageBlockSize;
    private BlockResponse latestBlock;
    private BlockResponse genesisBlock;
}
