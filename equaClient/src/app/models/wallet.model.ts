export interface WalletDTO {
  id: string;
  balance: number;
  ownerId: number;
  createdAt: Date;
  updatedAt: Date;
}

export interface DinarWallet {
  walletId: string;
  balance: number;
  ownerId: number;
  dinars: Dinar[];
  createdAt: Date;
  updatedAt: Date;
}

export interface Dinar {
  serialNumber: string;
  value: number;
  issueDate: Date;
  ownerWalletId: string;
}