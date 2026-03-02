package com.rayen.blockChainManagement.model;

import com.rayen.blockChainManagement.entity.Block;
import com.rayen.blockChainManagement.entity.Transaction; // ← fix #1: your entity, not jakarta

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BlockMAPPER {

    public static TransactionDTO toTransactionDTO(Transaction t) {
        return new TransactionDTO(
                t.getTransactionId(),
                t.getFromWallet(),
                t.getToWallet(),
                t.getAmount(),
                t.getTimestamp(),
                t.getStatus(),
                t.getTransactionHash(),
                t.getFee()
        );
    }

    public static BlockDTO toBlockDTO(Block block) {
        List<TransactionDTO> txDTOs = block.getTransaction() == null
                ? Collections.emptyList()
                : block.getTransaction().stream()
                .map(BlockMAPPER::toTransactionDTO)
                .collect(Collectors.toList());

        return new BlockDTO(
                block.getBlockId(),
                block.getPreviousHash(),
                block.getBlockHash(),
                block.getTimestamp(),
                block.getBlockSize(),
                block.getPreviousBlock() != null ? block.getPreviousBlock().getBlockId() : null,
                txDTOs,
                block.getCreatedAt(),
                block.getUpdatedAt()
        );
    }
}