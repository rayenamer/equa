export interface AssetResponseFinancial {
    id: number;
    name: string;
    ticker: string;
    category: string;
    description: string;
    logoUrl: string;
    currentPriceEqua: number;
    totalSupply: number;
    circulatingSupply: number;
    volume24h: number;
    status: string;
    verified: boolean;
    createdAt: string | null;
}

export interface AssetRequestFinancial {
    name: string;
    ticker: string;
    description: string;
    initialPrice: number; // or initialPriceEqua if consistent
    totalSupply: number;
    category: string;
}

export interface PriceHistoryResponseFinancial {
    timestamp: string;
    price: number;
}

export interface PortfolioResponseFinancial {
    userId: number;
    totalValueEqua: number;
    items: PortfolioAssetResponseFinancial[];
}

export interface PortfolioAssetResponseFinancial {
    assetName: string;
    ticker: string;
    quantity: number;
    avgBuyPriceEqua: number;
    currentPriceEqua: number;
    totalValueEqua: number;
    pnlEqua: number;
}

export interface TransactionResponseFinancial {
    id: number;
    type: 'BUY' | 'SELL';
    ticker: string;
    quantity: number;
    pricePerUnitEqua: number;
    totalEqua: number | null;
    feesEqua: number | null;
    createdAt: string | null;
}

export interface TradeRequestFinancial {
    assetId: number;
    amountEqua: number;
}

export interface TradeResponseFinancial {
    transactionId: number;
    type: 'BUY' | 'SELL';
    ticker: string;
    quantity: number;
    pricePerUnitEqua: number;
    totalEqua: number;
    feesEqua: number;
    createdAt: string;
}
