export interface HealthScore {
    decentralizationScore: number;
    decentralizationExplanation: string;
    activityScore: number;
    activityExplanation: string;
    nodeDiversityScore: number;
    nodeDiversityExplanation: string;
    overallScore: number;
    overallExplanation: string;
}

export interface NodeOdds {
    nodeId: number;
    location: string;
    winChance: number;
    reason: string;
}

export interface ValidatorPrediction {
    predictedNodeId: number;
    location: string;
    nodeType: string;
    reputation: number;
    winProbability: number;
    reasoning: string;
    allNodeOdds: NodeOdds[];
}
