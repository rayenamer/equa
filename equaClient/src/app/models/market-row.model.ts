export type MarketType = 'CEX' | 'DEX';
export type PairType = 'spot' | 'perpetual' | 'futures';

export interface MarketRow {
  rank: number;
  exchange: string;
  exchangeLogo?: string;
  pair: string;
  pairLink?: string;
  price: string;
  depthBid: string;
  depthAsk: string;
  volume24h: string;
  volumePercent: string;
  liquidity: string;
  liquidityIsValue?: boolean;
  type: MarketType;
  pairType: PairType;
  tokenSymbol?: string;
}
