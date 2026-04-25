export interface Dinar {
    id?: number;
    serialNumber: string;
    value: number;
    issueDate: Date;
    ownerWalletId: string;
}

export interface DinarWallet {
     walletId: string;
    userId: string;
    balance: number;
    status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED'; // adjust if needed
    createdAt: Date;
    updatedAt: Date;
}
export type WalletStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
export type LoyaltyTier = 'BRONZE' | 'SILVER' | 'GOLD' | 'PLATINUM';
export type FraudRiskLevel = 'LOW' | 'MEDIUM' | 'HIGH';

export interface CurrencyBalances {
  [currency: string]: number; // EUR, USD, TND, BTC...
}

export interface WalletDTO {
  id: number;
  publicKey: string;
  status: WalletStatus;
  balance: number;
  equaAmount: number;
  ownerEmail: string;
  loyaltyTier: LoyaltyTier;
  loyaltyPoints: number;
  fraudRiskLevel: FraudRiskLevel;
  achievements: any[];          // refine later when structure is known
  completedChallenges: any[];   // refine later
  currencyBalances: CurrencyBalances;
}