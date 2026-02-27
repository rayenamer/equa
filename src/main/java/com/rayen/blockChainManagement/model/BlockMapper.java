package com.rayen.blockChainManagement.model;


import com.rayen.blockChainManagement.entity.Block;
import com.rayen.blockChainManagement.model.BlockResponse;
import com.rayen.blockChainManagement.model.BlockStats;
import org.springframework.stereotype.Component;

@Component
public class BlockMapper {

    public BlockResponse toResponse(Block block) {
        if (block == null) {
            return null;
        }

        return BlockResponse.builder()
                .blockId(block.getBlockId())
                .previousHash(block.getPreviousHash())
                .blockHash(block.getBlockHash())
                .timestamp(block.getTimestamp())
                .blockSize(block.getBlockSize())
                .previousBlockId(block.getPreviousBlock() != null ?
                        block.getPreviousBlock().getBlockId() : null)
                .nextBlockId(block.getNextBlock() != null ?
                        block.getNextBlock().getBlockId() : null)
                .createdAt(block.getCreatedAt())
                .updatedAt(block.getUpdatedAt())
                .build();
    }

    public BlockResponse toResponseWithoutNavigation(Block block) {
        if (block == null) {
            return null;
        }

        return BlockResponse.builder()
                .blockId(block.getBlockId())
                .previousHash(block.getPreviousHash())
                .blockHash(block.getBlockHash())
                .timestamp(block.getTimestamp())
                .blockSize(block.getBlockSize())
                .createdAt(block.getCreatedAt())
                .updatedAt(block.getUpdatedAt())
                .build();
    }

    public BlockStats toStats(Long totalBlocks, Double averageBlockSize,
                              BlockResponse latestBlock, BlockResponse genesisBlock) {
        return BlockStats.builder()
                .totalBlocks(totalBlocks)
                .averageBlockSize(averageBlockSize)
                .latestBlock(latestBlock)
                .genesisBlock(genesisBlock)
                .build();
    }

    public void updateBlockFromRequest(Block target, Block source) {
        if (source == null || target == null) {
            return;
        }

        if (source.getPreviousHash() != null) {
            target.setPreviousHash(source.getPreviousHash());
        }
        if (source.getBlockHash() != null) {
            target.setBlockHash(source.getBlockHash());
        }
        if (source.getTimestamp() != null) {
            target.setTimestamp(source.getTimestamp());
        }
        if (source.getBlockSize() != null) {
            target.setBlockSize(source.getBlockSize());
        }
        if (source.getPreviousBlock() != null) {
            target.setPreviousBlock(source.getPreviousBlock());
        }
    }
}
