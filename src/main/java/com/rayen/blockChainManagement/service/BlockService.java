package com.rayen.blockChainManagement.service;

import com.rayen.blockChainManagement.entity.Block;
import com.rayen.blockChainManagement.model.BlockMapper;
import com.rayen.blockChainManagement.model.BlockRequest;
import com.rayen.blockChainManagement.model.BlockResponse;
import com.rayen.blockChainManagement.model.BlockStats;
import com.rayen.blockChainManagement.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockService {

    private final BlockRepository blockRepository;
    private final BlockMapper blockMapper;
    private static final String GENESIS_PREVIOUS_HASH = "0000000000000000000000000000000000000000000000000000000000000000";
    @Transactional
    public BlockResponse createBlock(BlockRequest request) {
        log.info("Creating new block with previousHash: {}", request.getPreviousHash());

        boolean isGenesisBlock = blockRepository.countTotalBlocks() == 0;

        Block block = new Block();
        block.setTimestamp(LocalDateTime.now());
        block.setCreatedAt(LocalDateTime.now());
        block.setUpdatedAt(LocalDateTime.now());

        if (isGenesisBlock) {
            log.info("Creating GENESIS block");
            block.setPreviousHash(GENESIS_PREVIOUS_HASH);
            block.setPreviousBlock(null);
        } else {
            Block previousBlock = blockRepository.findLatestBlock()
                    .orElseThrow(() -> new IllegalStateException("No blocks found in blockchain"));
            block.setPreviousBlock(previousBlock);
            block.setPreviousHash(previousBlock.getBlockHash());
        }

        String blockData = buildBlockData(block);
        block.setBlockHash(calculateHash(blockData));

        block.setBlockSize(calculateBlockSize(block));

        Block savedBlock = blockRepository.save(block);
        log.info("Block created successfully with hash: {}", savedBlock.getBlockHash());

        return blockMapper.toResponse(savedBlock);
    }
    @Transactional(readOnly = true)
    public BlockResponse getBlockById(Integer blockId) {
        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new IllegalArgumentException("Block not found with ID: " + blockId));
        return blockMapper.toResponse(block);
    }

    @Transactional(readOnly = true)
    public BlockResponse getBlockByHash(String blockHash) {
        Block block = blockRepository.findByBlockHash(blockHash)
                .orElseThrow(() -> new IllegalArgumentException("Block not found with hash: " + blockHash));
        return blockMapper.toResponse(block);
    }

    @Transactional(readOnly = true)
    public BlockResponse getLatestBlock() {
        Block block = blockRepository.findLatestBlock()
                .orElseThrow(() -> new IllegalStateException("No blocks found in blockchain"));
        return blockMapper.toResponse(block);
    }

    @Transactional(readOnly = true)
    public BlockResponse getGenesisBlock() {
        Block block= blockRepository.findGenesisBlock();
        return blockMapper.toResponse(block);
    }

    @Transactional(readOnly = true)
    public List<BlockResponse> searchBlocks(BlockRequest request) {
        List<Block> blocks = blockRepository.findBlocksByOptionalParams(
                request.getBlockHash(),
                request.getPreviousHash(),
                request.getMinBlockSize(),
                request.getMaxBlockSize(),
                null, // minTransactionCount - will be added later
                null, // maxTransactionCount - will be added later
                request.getTimestampAfter(),
                request.getTimestampBefore(),
                request.getCreatedAfter(),
                request.getCreatedBefore(),
                request.getSortBy() != null ? request.getSortBy() : "timestampDesc"
        );

        return blocks.stream()
                .map(blockMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BlockResponse> getAllBlocks() {
        List<Block> blocks = blockRepository.findAllByOrderByTimestampDesc();
        return blocks.stream()
                .map(blockMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BlockStats getBlockchainStats() {
        Long totalBlocks = blockRepository.countTotalBlocks();
        Double averageBlockSize = blockRepository.getAverageBlockSize();

        BlockResponse latestBlock = null;
        BlockResponse genesisBlock = null;

        try {
            latestBlock = getLatestBlock();
        } catch (Exception e) {
            log.warn("Could not fetch latest block", e);
        }

        try {
            genesisBlock = getGenesisBlock();
        } catch (Exception e) {
            log.warn("Could not fetch genesis block", e);
        }

        return blockMapper.toStats(totalBlocks, averageBlockSize, latestBlock, genesisBlock);
    }

    public boolean blockExists(String blockHash) {
        return blockRepository.existsByBlockHash(blockHash);
    }

    // ==================== HELPER METHODS ====================

    private String buildBlockData(Block block) {
        StringBuilder data = new StringBuilder();
        data.append(block.getPreviousHash() != null ? block.getPreviousHash() : "");
        data.append(block.getTimestamp() != null ? block.getTimestamp().toString() : "");

        return data.toString();
    }

    private String calculateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private Long calculateBlockSize(Block block) {
        java.security.SecureRandom random = new java.security.SecureRandom();
        return (long) (2 + random.nextInt(5));
    }


}