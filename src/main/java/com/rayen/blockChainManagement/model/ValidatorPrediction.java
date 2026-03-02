package com.rayen.blockChainManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class ValidatorPrediction {
    private Integer predictedNodeId;
    private String location;
    private String nodeType;
    private double reputation;
    private int winProbability; // 0-100
    private String reasoning;
    private List<NodeOdds> allNodeOdds;

    @Data
    @AllArgsConstructor
    public static class NodeOdds {
        private Integer nodeId;
        private String location;
        private int winChance; // 0-100
        private String reason;
    }
}