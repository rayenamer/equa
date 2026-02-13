package com.rayen.blockChainManagement.controller;


import com.rayen.blockChainManagement.entity.Node;
import com.rayen.blockChainManagement.service.NodeService;
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
@CrossOrigin(origins = "*")
public class NodeController {

    private final NodeService nodeService;

    @PostMapping
    public ResponseEntity<Node> createNode(@RequestBody Node node) {
        log.info("REST request to create node");
        try {
            Node createdNode = nodeService.createNode(node);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNode);
        } catch (IllegalArgumentException e) {
            log.error("Error creating node: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Node> getNodeById(@PathVariable Integer id) {
        log.info("REST request to get node: {}", id);
        return nodeService.getNodeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Node>> getAllNodes() {
        log.info("REST request to get all nodes");
        List<Node> nodes = nodeService.getAllNodes();
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/ip/{ipAddress}")
    public ResponseEntity<Node> getNodeByIpAddress(@PathVariable String ipAddress) {
        log.info("REST request to get node by IP: {}", ipAddress);
        return nodeService.getNodeByIpAddress(ipAddress)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/public-key/{publicKey}")
    public ResponseEntity<Node> getNodeByPublicKey(@PathVariable String publicKey) {
        log.info("REST request to get node by public key");
        return nodeService.getNodeByPublicKey(publicKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{nodeType}")
    public ResponseEntity<List<Node>> getNodesByType(@PathVariable String nodeType) {
        log.info("REST request to get nodes by type: {}", nodeType);
        List<Node> nodes = nodeService.getNodesByType(nodeType);
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Node>> getNodesByStatus(@PathVariable String status) {
        log.info("REST request to get nodes by status: {}", status);
        List<Node> nodes = nodeService.getNodesByStatus(status);
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/online")
    public ResponseEntity<List<Node>> getOnlineNodes() {
        log.info("REST request to get online nodes");
        List<Node> nodes = nodeService.getOnlineNodes();
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/offline")
    public ResponseEntity<List<Node>> getOfflineNodes() {
        log.info("REST request to get offline nodes");
        List<Node> nodes = nodeService.getOfflineNodes();
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<Node>> getNodesByLocation(@PathVariable String location) {
        log.info("REST request to get nodes by location: {}", location);
        List<Node> nodes = nodeService.getNodesByLocation(location);
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Node>> getNodesByStatusAndType(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location) {
        log.info("REST request to get nodes by filters - status: {}, type: {}, location: {}",
                status, type, location);

        if (status != null && type != null) {
            List<Node> nodes = nodeService.getNodesByStatusAndType(status, type);
            return ResponseEntity.ok(nodes);
        } else if (status != null && location != null) {
            List<Node> nodes = nodeService.getNodesByStatusAndLocation(status, location);
            return ResponseEntity.ok(nodes);
        } else if (status != null) {
            List<Node> nodes = nodeService.getNodesByStatus(status);
            return ResponseEntity.ok(nodes);
        } else if (type != null) {
            List<Node> nodes = nodeService.getNodesByType(type);
            return ResponseEntity.ok(nodes);
        } else if (location != null) {
            List<Node> nodes = nodeService.getNodesByLocation(location);
            return ResponseEntity.ok(nodes);
        }

        return ResponseEntity.ok(nodeService.getAllNodes());
    }

    @GetMapping("/top-reputation")
    public ResponseEntity<List<Node>> getTopNodesByReputation() {
        log.info("REST request to get top nodes by reputation");
        List<Node> nodes = nodeService.getTopNodesByReputation();
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/reputation/min/{minScore}")
    public ResponseEntity<List<Node>> getNodesByMinimumReputation(@PathVariable Double minScore) {
        log.info("REST request to get nodes with minimum reputation: {}", minScore);
        List<Node> nodes = nodeService.getNodesByMinimumReputation(minScore);
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/reputation/range")
    public ResponseEntity<List<Node>> getNodesByReputationRange(
            @RequestParam Double min,
            @RequestParam Double max) {
        log.info("REST request to get nodes by reputation range: {} - {}", min, max);
        List<Node> nodes = nodeService.getNodesByReputationRange(min, max);
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/validators")
    public ResponseEntity<List<Node>> getValidatorNodes() {
        log.info("REST request to get validator nodes");
        List<Node> nodes = nodeService.getValidatorNodes();
        return ResponseEntity.ok(nodes);
    }


    @GetMapping("/active-after")
    public ResponseEntity<List<Node>> getNodesActiveAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        log.info("REST request to get nodes active after: {}", date);
        List<Node> nodes = nodeService.getNodesActiveAfter(date);
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/inactive-before")
    public ResponseEntity<List<Node>> getNodesInactiveBefore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        log.info("REST request to get nodes inactive before: {}", date);
        List<Node> nodes = nodeService.getNodesInactiveBefore(date);
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/created-after")
    public ResponseEntity<List<Node>> getNodesCreatedAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        log.info("REST request to get nodes created after: {}", date);
        List<Node> nodes = nodeService.getNodesCreatedAfter(date);
        return ResponseEntity.ok(nodes);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Node> updateNodeStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        log.info("REST request to update node {} status to: {}", id, status);
        try {
            Node updatedNode = nodeService.updateNodeStatus(id, status);
            return ResponseEntity.ok(updatedNode);
        } catch (IllegalArgumentException e) {
            log.error("Error updating node status: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


    @PatchMapping("/{id}/reputation")
    public ResponseEntity<Node> updateReputationScore(
            @PathVariable Integer id,
            @RequestParam Double score) {
        log.info("REST request to update node {} reputation to: {}", id, score);
        try {
            Node updatedNode = nodeService.updateReputationScore(id, score);
            return ResponseEntity.ok(updatedNode);
        } catch (IllegalArgumentException e) {
            log.error("Error updating node reputation: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/heartbeat")
    public ResponseEntity<Node> updateLastSeen(@PathVariable Integer id) {
        log.info("REST request to update node {} last seen", id);
        try {
            Node updatedNode = nodeService.updateLastSeen(id);
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