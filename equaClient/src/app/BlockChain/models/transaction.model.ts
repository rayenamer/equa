export enum TransactionStatus {
    COMPLETED = 'COMPLETED',
    PENDING = 'PENDING',
    FAILED = 'FAILED',
    PROCESSING = 'PROCESSING'
}

export interface Transaction {
    transactionId: number;
    fromWallet: string;
    toWallet: string;
    amount: number;
    timestamp: Date;
    status: TransactionStatus;
    transactionHash: string;
    fee: number;
}

export interface TransactionRequest {
    transactionId?: number;
    fromWallet: string;
    toWallet: string;
    amount: number;
    timestamp?: string | Date;
}

