package com.rayen.blockChainManagement.service;

import com.rayen.blockChainManagement.model.NodeRequest;
import com.rayen.blockChainManagement.model.NodeResponse;
import com.rayen.blockChainManagement.model.NodeMapper;
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
    private final NodeMapper nodeMapper;

    public NodeResponse createNode(NodeRequest request) {
        log.info("Creating new node with IP: {}", request.getIpAddress());

        if (nodeRepository.existsByIpAddress(request.getIpAddress())) {
            throw new IllegalArgumentException("Node with IP address " + request.getIpAddress() + " already exists");
        }

        if (request.getPublicKey() != null && nodeRepository.existsByPublicKey(request.getPublicKey())) {
            throw new IllegalArgumentException("Node with public key already exists");
        }

        Node node = nodeMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();
        node.setCreatedAt(now);
        node.setUpdatedAt(now);
        node.setLastSeen(now);
        node.setReputationScore(0.0);
        node.setStatus("ONLINE");

        Node savedNode = nodeRepository.save(node);
        return nodeMapper.toResponse(savedNode);
    }

    @Transactional(readOnly = true)
    public Optional<NodeResponse> getNodeById(Integer nodeId) {
        log.debug("Fetching node with ID: {}", nodeId);
        return nodeRepository.findById(nodeId)
                .map(nodeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<NodeResponse> getNodesByOptionalParams(
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
        log.debug("Fetching nodes with optional params");

        List<Node> nodes;

        // Handle hasTransaction separately
        if (hasTransaction != null) {
            if (hasTransaction) {
                nodes = nodeRepository.findNodesWithTransaction();
            } else {
                nodes = nodeRepository.findNodesWithoutTransaction();
            }

            // Apply additional filters in memory if hasTransaction is specified
            nodes = nodes.stream()
                    .filter(n -> status == null || status.equals(n.getStatus()))
                    .filter(n -> publicKey == null || publicKey.equals(n.getPublicKey()))
                    .filter(n -> nodeType == null || nodeType.equals(n.getNodeType()))
                    .filter(n -> location == null || location.equals(n.getLocation()))
                    .filter(n -> ipAddress == null || ipAddress.equals(n.getIpAddress()))
                    .filter(n -> minReputationScore == null || n.getReputationScore() >= minReputationScore)
                    .filter(n -> maxReputationScore == null || n.getReputationScore() <= maxReputationScore)
                    .filter(n -> lastSeenAfter == null || n.getLastSeen().isAfter(lastSeenAfter))
                    .filter(n -> lastSeenBefore == null || n.getLastSeen().isBefore(lastSeenBefore))
                    .filter(n -> createdAfter == null || n.getCreatedAt().isAfter(createdAfter))
                    .toList();
        } else {
            // Use the main query when hasTransaction is not specified
            nodes = nodeRepository.findNodesByOptionalParams(
                    status, publicKey, nodeType, location, ipAddress,
                    minReputationScore, maxReputationScore,
                    lastSeenAfter, lastSeenBefore, createdAfter
            );
        }

        return nodeMapper.toResponseList(nodes);
    }

    @Transactional(readOnly = true)
    public List<NodeResponse> getAllNodes() {
        log.debug("Fetching all nodes");
        List<Node> nodes = nodeRepository.findAll();
        return nodeMapper.toResponseList(nodes);
    }

    @Transactional(readOnly = true)
    public List<NodeResponse> getOnlineNodes() {
        log.debug("Fetching online nodes");
        List<Node> nodes = nodeRepository.findOnlineNodes();
        return nodeMapper.toResponseList(nodes);
    }

    @Transactional(readOnly = true)
    public List<NodeResponse> getTopNodesByReputation() {
        log.debug("Fetching top nodes by reputation");
        List<Node> nodes = nodeRepository.findTopNodesByReputation();
        return nodeMapper.toResponseList(nodes);
    }

    public NodeResponse updateNodeStatus(Integer nodeId, String status) {
        log.info("Updating node {} status to: {}", nodeId, status);

        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found with ID: " + nodeId));

        node.setStatus(status);
        node.setUpdatedAt(LocalDateTime.now());

        if ("ONLINE".equals(status)) {
            node.setLastSeen(LocalDateTime.now());
        }

        Node updatedNode = nodeRepository.save(node);
        return nodeMapper.toResponse(updatedNode);
    }

    public NodeResponse updateReputationScore(Integer nodeId, Double reputationScore) {
        log.info("Updating node {} reputation score to: {}", nodeId, reputationScore);

        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found with ID: " + nodeId));

        node.setReputationScore(reputationScore);
        node.setUpdatedAt(LocalDateTime.now());

        Node updatedNode = nodeRepository.save(node);
        return nodeMapper.toResponse(updatedNode);
    }

    public NodeResponse updateLastSeen(Integer nodeId) {
        log.debug("Updating last seen for node: {}", nodeId);

        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found with ID: " + nodeId));

        node.setLastSeen(LocalDateTime.now());
        node.setUpdatedAt(LocalDateTime.now());

        Node updatedNode = nodeRepository.save(node);
        return nodeMapper.toResponse(updatedNode);
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
        return nodeRepository.existsByIpAddress(ipAddress);
    }

    @Transactional(readOnly = true)
    public boolean existsByPublicKey(String publicKey) {
        return nodeRepository.existsByPublicKey(publicKey);
    }
}