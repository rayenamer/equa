package com.rayen.blockChainManagement.controller;

import com.rayen.blockChainManagement.model.NodeRequest;
import com.rayen.blockChainManagement.model.NodeResponse;
import com.rayen.blockChainManagement.service.NodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/nodes")
@RequiredArgsConstructor
@Slf4j
public class NodeController {

    private final NodeService nodeService;

    @PostMapping
    public ResponseEntity<NodeResponse> createNode(@Valid @RequestBody NodeRequest request) {
        log.info("REST request to create node");
        try {
            NodeResponse createdNode = nodeService.createNode(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNode);
        } catch (IllegalArgumentException e) {
            log.error("Error creating node: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<NodeResponse> getNodeById(@PathVariable Integer id) {
        log.info("REST request to get node: {}", id);
        return nodeService.getNodeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<NodeResponse>> getAllNodes() {
        log.info("REST request to get all nodes");
        List<NodeResponse> nodes = nodeService.getAllNodes();
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/search")
    public ResponseEntity<List<NodeResponse>> searchNodes(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String publicKey,
            @RequestParam(required = false) String nodeType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) Double minReputationScore,
            @RequestParam(required = false) Double maxReputationScore,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastSeenAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastSeenBefore,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAfter,
            @RequestParam(required = false) Boolean hasTransaction) {

        log.info("REST request to search nodes");

        List<NodeResponse> nodes = nodeService.getNodesByOptionalParams(
                status, publicKey, nodeType, location, ipAddress,
                minReputationScore, maxReputationScore,
                lastSeenAfter, lastSeenBefore, createdAfter, hasTransaction
        );

        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/online")
    public ResponseEntity<List<NodeResponse>> getOnlineNodes() {
        log.info("REST request to get online nodes");
        List<NodeResponse> nodes = nodeService.getOnlineNodes();
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/top-reputation")
    public ResponseEntity<List<NodeResponse>> getTopNodesByReputation() {
        log.info("REST request to get top nodes by reputation");
        List<NodeResponse> nodes = nodeService.getTopNodesByReputation();
        return ResponseEntity.ok(nodes);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<NodeResponse> updateNodeStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        log.info("REST request to update node {} status to: {}", id, status);
        try {
            NodeResponse updatedNode = nodeService.updateNodeStatus(id, status);
            return ResponseEntity.ok(updatedNode);
        } catch (IllegalArgumentException e) {
            log.error("Error updating node status: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/reputation")
    public ResponseEntity<NodeResponse> updateReputationScore(
            @PathVariable Integer id,
            @RequestParam Double score) {
        log.info("REST request to update node {} reputation to: {}", id, score);
        try {
            NodeResponse updatedNode = nodeService.updateReputationScore(id, score);
            return ResponseEntity.ok(updatedNode);
        } catch (IllegalArgumentException e) {
            log.error("Error updating node reputation: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/heartbeat")
    public ResponseEntity<NodeResponse> updateLastSeen(@PathVariable Integer id) {
        log.info("REST request to update node {} last seen", id);
        try {
            NodeResponse updatedNode = nodeService.updateLastSeen(id);
            return ResponseEntity.ok(updatedNode);
        } catch (IllegalArgumentException e) {
            log.error("Error updating node last seen: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countNodesByStatus(@PathVariable String status) {
        log.info("REST request to count nodes by status: {}", status);
        long count = nodeService.countNodesByStatus(status);
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNode(@PathVariable Integer id) {
        log.info("REST request to delete node: {}", id);
        try {
            nodeService.deleteNode(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting node: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/ip/{ipAddress}")
    public ResponseEntity<Boolean> existsByIpAddress(@PathVariable String ipAddress) {
        log.info("REST request to check if node exists with IP: {}", ipAddress);
        boolean exists = nodeService.existsByIpAddress(ipAddress);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/public-key/{publicKey}")
    public ResponseEntity<Boolean> existsByPublicKey(@PathVariable String publicKey) {
        log.info("REST request to check if node exists with public key");
        boolean exists = nodeService.existsByPublicKey(publicKey);
        return ResponseEntity.ok(exists);
    }
}