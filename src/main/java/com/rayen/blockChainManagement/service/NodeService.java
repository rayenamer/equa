package com.rayen.blockChainManagement.service;


import com.rayen.blockChainManagement.entity.Node;
import com.rayen.blockChainManagement.repository.NodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NodeService {

    private final NodeRepository nodeRepository;

    public Node createNode(Node node) {
        log.info("Creating new node with IP: {}", node.getIpAddress());

        if (!getNodesByOptionalParams(null, null, null, null, node.getIpAddress(), null, null, null, null, null, null).isEmpty()) {
            throw new IllegalArgumentException("Node with IP address " + node.getIpAddress() + " already exists");
        }

        if (node.getPublicKey() != null && !getNodesByOptionalParams(null, node.getPublicKey(), null, null, null, null, null, null, null, null, null).isEmpty()) {
            throw new IllegalArgumentException("Node with public key already exists");
        }

        LocalDateTime now = LocalDateTime.now();
        node.setCreatedAt(now);
        node.setUpdatedAt(now);
        node.setLastSeen(now);

        if (node.getReputationScore() == null) {
            node.setReputationScore(0.0);
        }
        if (node.getStatus() == null) {
            node.setStatus("ONLINE");
        }

        return nodeRepository.save(node);
    }

    @Transactional(readOnly = true)
    public Optional<Node> getNodeById(Integer nodeId) {
        log.debug("Fetching node with ID: {}", nodeId);
        return nodeRepository.findById(nodeId);
    }

    // Unified method with all optional parameters
    @Transactional(readOnly = true)
    public List<Node> getNodesByOptionalParams(
            String status,
            String publicKey,
            String nodeType,
            String location,
            String ipAddress,
            Double minReputationScore,
            Double maxReputationScore,
            LocalDateTime lastSeenAfter,
            LocalDateTime lastSeenBefore,
            LocalDateTime createdAfter,
            Boolean hasTransaction
    ) {
        log.debug("Fetching nodes with optional params - status: {}, publicKey: {}, nodeType: {}, location: {}, ipAddress: {}, minReputation: {}, maxReputation: {}, lastSeenAfter: {}, lastSeenBefore: {}, createdAfter: {}, hasTransaction: {}",
                status, publicKey != null ? "***" : null, nodeType, location, ipAddress, minReputationScore, maxReputationScore, lastSeenAfter, lastSeenBefore, createdAfter, hasTransaction);

        return nodeRepository.findNodesByOptionalParams(
                status, publicKey, nodeType, location, ipAddress,
                minReputationScore, maxReputationScore,
                lastSeenAfter, lastSeenBefore, createdAfter, hasTransaction
        );
    }

    @Transactional(readOnly = true)
    public List<Node> getAllNodes() {
        log.debug("Fetching all nodes");
        return nodeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Node> getOnlineNodes() {
        log.debug("Fetching online nodes");
        return nodeRepository.findOnlineNodes();
    }

    @Transactional(readOnly = true)
    public List<Node> getTopNodesByReputation() {
        log.debug("Fetching top nodes by reputation");
        return nodeRepository.findTopNodesByReputation();
    }

    public Node updateNodeStatus(Integer nodeId, String status) {
        log.info("Updating node {} status to: {}", nodeId, status);

        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found with ID: " + nodeId));

        node.setStatus(status);
        node.setUpdatedAt(LocalDateTime.now());

        if ("ONLINE".equals(status)) {
            node.setLastSeen(LocalDateTime.now());
        }

        return nodeRepository.save(node);
    }

    public Node updateReputationScore(Integer nodeId, Double reputationScore) {
        log.info("Updating node {} reputation score to: {}", nodeId, reputationScore);

        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found with ID: " + nodeId));

        node.setReputationScore(reputationScore);
        node.setUpdatedAt(LocalDateTime.now());

        return nodeRepository.save(node);
    }

    public Node updateLastSeen(Integer nodeId) {
        log.debug("Updating last seen for node: {}", nodeId);

        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found with ID: " + nodeId));

        node.setLastSeen(LocalDateTime.now());
        node.setUpdatedAt(LocalDateTime.now());

        return nodeRepository.save(node);
    }

    @Transactional(readOnly = true)
    public long countNodesByStatus(String status) {
        log.debug("Counting nodes by status: {}", status);
        return nodeRepository.countByStatus(status);
    }

    public void deleteNode(Integer nodeId) {
        log.info("Deleting node with ID: {}", nodeId);

        if (!nodeRepository.existsById(nodeId)) {
            throw new IllegalArgumentException("Node not found with ID: " + nodeId);
        }

        nodeRepository.deleteById(nodeId);
    }

    @Transactional(readOnly = true)
    public boolean existsByIpAddress(String ipAddress) {
        return !getNodesByOptionalParams(null, null, null, null, ipAddress, null, null, null, null, null, null).isEmpty();
    }

    @Transactional(readOnly = true)
    public boolean existsByPublicKey(String publicKey) {
        return !getNodesByOptionalParams(null, publicKey, null, null, null, null, null, null, null, null, null).isEmpty();
    }
}