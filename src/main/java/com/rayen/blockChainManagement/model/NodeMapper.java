package com.rayen.blockChainManagement.model;

import com.rayen.blockChainManagement.entity.Node;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NodeMapper {

    public Node toEntity(NodeRequest request) {
        if (request == null) {
            return null;
        }

        Node node = new Node();
        node.setNodeType(request.getNodeType());
        node.setIpAddress(request.getIpAddress());
        node.setPublicKey(request.getPublicKey());
        node.setLocation(request.getLocation());

        return node;
    }

    public NodeResponse toResponse(Node node) {
        if (node == null) {
            return null;
        }

        return NodeResponse.builder()
                .nodeId(node.getNodeId())
                .nodeType(node.getNodeType())
                .ipAddress(node.getIpAddress())
                .status(node.getStatus())
                .publicKey(node.getPublicKey())
                .reputationScore(node.getReputationScore())
                .lastSeen(node.getLastSeen())
                .location(node.getLocation())
                .connectedNodesCount(node.getConnectedNodes() != null ? node.getConnectedNodes().size() : 0)
                .hasTransaction(node.getTransaction() != null)
                .createdAt(node.getCreatedAt())
                .updatedAt(node.getUpdatedAt())
                .build();
    }

    public List<NodeResponse> toResponseList(List<Node> nodes) {
        if (nodes == null) {
            return null;
        }

        return nodes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateEntityFromRequest(NodeRequest request, Node node) {
        if (request == null || node == null) {
            return;
        }

        if (request.getNodeType() != null) {
            node.setNodeType(request.getNodeType());
        }
        if (request.getIpAddress() != null) {
            node.setIpAddress(request.getIpAddress());
        }
        if (request.getPublicKey() != null) {
            node.setPublicKey(request.getPublicKey());
        }
        if (request.getLocation() != null) {
            node.setLocation(request.getLocation());
        }
    }
}