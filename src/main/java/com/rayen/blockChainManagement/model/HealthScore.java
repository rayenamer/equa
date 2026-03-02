package com.rayen.blockChainManagement.model;

import lombok.Data;

@Data
public class HealthScore {
    private int decentralizationScore;
    private String decentralizationExplanation;

    private int activityScore;
    private String activityExplanation;

    private int nodeDiversityScore;
    private String nodeDiversityExplanation;

    private int overallScore;
    private String overallExplanation;
}