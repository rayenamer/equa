package com.rayen.blockChainManagement.controller;


import com.rayen.blockChainManagement.model.BlockRequest;
import com.rayen.blockChainManagement.model.BlockResponse;
import com.rayen.blockChainManagement.model.BlockStats;
import com.rayen.blockChainManagement.service.BlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blocks")
@RequiredArgsConstructor
@Slf4j
public class BlockController {

    private final BlockService blockService;

    @PostMapping
    public ResponseEntity<BlockResponse> createBlock(@RequestBody(required = false) BlockRequest request) {
        log.info("REST request to create block");

        if (request == null) {
            request = new BlockRequest();
        }

        BlockResponse response = blockService.createBlock(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlockResponse> getBlockById(@PathVariable Integer id) {
        log.info("REST request to get block by ID: {}", id);
        BlockResponse response = blockService.getBlockById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hash/{blockHash}")
    public ResponseEntity<BlockResponse> getBlockByHash(@PathVariable String blockHash) {
        log.info("REST request to get block by hash: {}", blockHash);
        BlockResponse response = blockService.getBlockByHash(blockHash);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BlockResponse>> getAllBlocks() {
        log.info("REST request to get all blocks");
        List<BlockResponse> blocks = blockService.getAllBlocks();
        return ResponseEntity.ok(blocks);
    }

    @PostMapping("/search")
    public ResponseEntity<List<BlockResponse>> searchBlocks(@RequestBody BlockRequest request) {
        log.info("REST request to search blocks with filters");
        List<BlockResponse> blocks = blockService.searchBlocks(request);
        return ResponseEntity.ok(blocks);
    }

    @GetMapping("/latest")
    public ResponseEntity<BlockResponse> getLatestBlock() {
        log.info("REST request to get latest block");
        BlockResponse response = blockService.getLatestBlock();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/genesis")
    public ResponseEntity<BlockResponse> getGenesisBlock() {
        log.info("REST request to get genesis block");
        BlockResponse response = blockService.getGenesisBlock();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<BlockStats> getBlockchainStats() {
        log.info("REST request to get blockchain statistics");
        BlockStats stats = blockService.getBlockchainStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/exists/{blockHash}")
    public ResponseEntity<Boolean> blockExists(@PathVariable String blockHash) {
        log.info("REST request to check if block exists: {}", blockHash);
        boolean exists = blockService.blockExists(blockHash);
        return ResponseEntity.ok(exists);
    }
}
