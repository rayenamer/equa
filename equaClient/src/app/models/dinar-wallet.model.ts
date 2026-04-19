export interface Dinar {
    id?: number;
    serialNumber: string;
    value: number;
    issueDate: Date;
    ownerWalletId: string;
}

export interface DinarWallet {
    walletId: string;
    balance: number;
    ownerId: number;
    dinars: Dinar[];
    createdAt: Date;
    updatedAt: Date;
}
