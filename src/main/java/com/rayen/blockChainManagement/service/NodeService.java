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

        if (nodeRepository.findByIpAddress(node.getIpAddress()).isPresent()) {
            throw new IllegalArgumentException("Node with IP address " + node.getIpAddress() + " already exists");
        }

        if (node.getPublicKey() != null && nodeRepository.findByPublicKey(node.getPublicKey()).isPresent()) {
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

    @Transactional(readOnly = true)
    public Optional<Node> getNodeByIpAddress(String ipAddress) {
        log.debug("Fetching node with IP: {}", ipAddress);
        return nodeRepository.findByIpAddress(ipAddress);
    }

    @Transactional(readOnly = true)
    public Optional<Node> getNodeByPublicKey(String publicKey) {
        log.debug("Fetching node with public key");
        return nodeRepository.findByPublicKey(publicKey);
    }

    @Transactional(readOnly = true)
    public List<Node> getAllNodes() {
        log.debug("Fetching all nodes");
        return nodeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Node> getNodesByType(String nodeType) {
        log.debug("Fetching nodes by type: {}", nodeType);
        return nodeRepository.findByNodeType(nodeType);
    }

    @Transactional(readOnly = true)
    public List<Node> getNodesByStatus(String status) {
        log.debug("Fetching nodes by status: {}", status);
        return nodeRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Node> getOnlineNodes() {
        log.debug("Fetching online nodes");
        return nodeRepository.findOnlineNodes();
    }

    @Transactional(readOnly = true)
    public List<Node> getOfflineNodes() {
        log.debug("Fetching offline nodes");
        return nodeRepository.findOfflineNodes();
    }

    @Transactional(readOnly = true)
    public List<Node> getNodesByLocation(String location) {
        log.debug("Fetching nodes by location: {}", location);
        return nodeRepository.findByLocation(location);
    }

    @Transactional(readOnly = true)
    public List<Node> getNodesByStatusAndType(String status, String nodeType) {
        log.debug("Fetching nodes by status: {} and type: {}", status, nodeType);
        return nodeRepository.findByStatusAndNodeType(status, nodeType);
    }

    @Transactional(readOnly = true)
    public List<Node> getNodesByStatusAndLocation(String status, String location) {
        log.debug("Fetching nodes by status: {} and location: {}", status, location);
        return nodeRepository.findByStatusAndLocation(status, location);
    }

    @Transactional(readOnly = true)
    public List<Node> getTopNodesByReputation() {
        log.debug("Fetching top nodes by reputation");
        return nodeRepository.findTopNodesByReputation();
    }

    @Transactional(readOnly = true)
    public List<Node> getNodesByMinimumReputation(Double minScore) {
        log.debug("Fetching nodes with minimum reputation: {}", minScore);
        return nodeRepository.findNodesByMinimumReputation(minScore);
    }

    @Transactional(readOnly = true)
    public List<Node> getNodesByReputationRange(Double minScore, Double maxScore) {
        log.debug("Fetching nodes by reputation range: {} - {}", minScore, maxScore);
        return nodeRepository.findByReputationScoreBetweenOrderByReputationScoreDesc(minScore, maxScore);
    }

    @Transactional(readOnly = true)
    public List<Node> getValidatorNodes() {
        log.debug("Fetching validator nodes");
        return nodeRepository.findValidatorNodes();
    }

    @Transactional(readOnly = true)
    public List<Node> getNodesActiveAfter(LocalDateTime dateTime) {
        log.debug("Fetching nodes active after: {}", dateTime);
        return nodeRepository.findByLastSeenAfter(dateTime);
    }

    @Transactional(readOnly = true)
    public List<Node> getNodesInactiveBefore(LocalDateTime dateTime) {
        log.debug("Fetching nodes inactive before: {}", dateTime);
        return nodeRepository.findByLastSeenBefore(dateTime);
    }

    @Transactional(readOnly = true)
    public List<Node> getNodesCreatedAfter(LocalDateTime date) {
        log.debug("Fetching nodes created after: {}", date);
        return nodeRepository.findByCreatedAtAfter(date);
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
        return nodeRepository.findByIpAddress(ipAddress).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean existsByPublicKey(String publicKey) {
        return nodeRepository.findByPublicKey(publicKey).isPresent();
    }
}
