export interface Block {
    blockId: number;
    previousHash: string;
    blockHash: string;
    timestamp: Date;
    blockSize: number;
    previousBlockId: number;
    createdAt: Date;
    updatedAt: Date;
}
