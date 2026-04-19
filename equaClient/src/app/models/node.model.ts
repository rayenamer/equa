export interface Node {
    nodeId: number;
    nodeType: string;
    ipAddress: string;
    status: string;
    publicKey: string;
    reputationScore: number;
    lastSeen: Date;
    location: string;
    connectedNodesCount: number;
    hasTransaction: boolean;
    createdAt: Date;
    updatedAt: Date;
}
