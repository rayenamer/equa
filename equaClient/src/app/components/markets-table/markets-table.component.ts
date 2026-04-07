import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import type { MarketRow, MarketType, PairType } from '../../models/market-row.model';

const MOCK_MARKETS: MarketRow[] = [
  {
    rank: 1,
    exchange: 'Binance',
    pair: 'EQUA/USDT',
    pairLink: '#',
    price: '€1,00',
    depthBid: '€12 450',
    depthAsk: '€11 200',
    volume24h: '€82 450',
    volumePercent: '18,5 %',
    liquidity: '1040',
    liquidityIsValue: false,
    type: 'CEX',
    pairType: 'spot',
    tokenSymbol: 'EQUA',
  },
  {
    rank: 2,
    exchange: 'Plateforme EQUA',
    pair: 'EQUA/EUR',
    pairLink: '#',
    price: '€1,00',
    depthBid: '€28 100',
    depthAsk: '€26 900',
    volume24h: '€65 200',
    volumePercent: '14,6 %',
    liquidity: '€125 400',
    liquidityIsValue: true,
    type: 'CEX',
    pairType: 'spot',
    tokenSymbol: 'EQUA',
  },
  {
    rank: 3,
    exchange: 'Uniswap',
    pair: 'EQUA/USDC',
    pairLink: '#',
    price: '€1,00',
    depthBid: '--',
    depthAsk: '--',
    volume24h: '€42 100',
    volumePercent: '9,4 %',
    liquidity: '€88 200',
    liquidityIsValue: true,
    type: 'DEX',
    pairType: 'spot',
    tokenSymbol: 'EQUA',
  },
  {
    rank: 4,
    exchange: 'PancakeSwap',
    pair: 'EQUA/BUSD',
    pairLink: '#',
    price: '€1,00',
    depthBid: '--',
    depthAsk: '--',
    volume24h: '€28 300',
    volumePercent: '6,3 %',
    liquidity: '€52 100',
    liquidityIsValue: true,
    type: 'DEX',
    pairType: 'spot',
    tokenSymbol: 'EQUA',
  },
  {
    rank: 5,
    exchange: 'Kraken',
    pair: 'EQUA/USD',
    pairLink: '#',
    price: '€1,00',
    depthBid: '€8 900',
    depthAsk: '€9 100',
    volume24h: '€35 600',
    volumePercent: '8,0 %',
    liquidity: '892',
    liquidityIsValue: false,
    type: 'CEX',
    pairType: 'spot',
    tokenSymbol: 'EQUA',
  },
  {
    rank: 6,
    exchange: 'Curve (Ethereum)',
    pair: 'EQUA/ETH',
    pairLink: '#',
    price: '≈1,00&nbsp;€',
    depthBid: '--',
    depthAsk: '--',
    volume24h: '€15 200',
    volumePercent: '3,4 %',
    liquidity: '€31 500',
    liquidityIsValue: true,
    type: 'DEX',
    pairType: 'spot',
    tokenSymbol: 'EQUA',
  },
  {
    rank: 7,
    exchange: 'Bybit',
    pair: 'EQUA/USDT',
    pairLink: '#',
    price: '€1,00',
    depthBid: '€5 200',
    depthAsk: '€5 800',
    volume24h: '€22 100',
    volumePercent: '5,0 %',
    liquidity: '756',
    liquidityIsValue: false,
    type: 'CEX',
    pairType: 'perpetual',
    tokenSymbol: 'EQUA',
  },
  {
    rank: 8,
    exchange: 'Gate.io',
    pair: 'EQUA/USDT',
    pairLink: '#',
    price: '€1,00',
    depthBid: '€4 100',
    depthAsk: '€4 300',
    volume24h: '€18 900',
    volumePercent: '4,2 %',
    liquidity: '621',
    liquidityIsValue: false,
    type: 'CEX',
    pairType: 'spot',
    tokenSymbol: 'EQUA',
  },
  {
    rank: 9,
    exchange: 'KuCoin',
    pair: 'EQUA/USDT',
    pairLink: '#',
    price: '€1,00',
    depthBid: '€3 800',
    depthAsk: '€4 000',
    volume24h: '€12 400',
    volumePercent: '2,8 %',
    liquidity: '534',
    liquidityIsValue: false,
    type: 'CEX',
    pairType: 'spot',
    tokenSymbol: 'EQUA',
  },
  {
    rank: 10,
    exchange: 'SushiSwap',
    pair: 'EQUA/WETH',
    pairLink: '#',
    price: '≈1,00&nbsp;€',
    depthBid: '--',
    depthAsk: '--',
    volume24h: '€8 500',
    volumePercent: '1,9 %',
    liquidity: '€19 200',
    liquidityIsValue: true,
    type: 'DEX',
    pairType: 'spot',
    tokenSymbol: 'EQUA',
  },
];

@Component({
  selector: 'app-markets-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './markets-table.component.html',
  styleUrls: ['./markets-table.component.scss'],
})
export class MarketsTableComponent {
  private allMarkets = MOCK_MARKETS;

  activeFilter = signal<'all' | MarketType | PairType>('all');
  activePairFilter = signal<'all' | PairType>('all');
  currentPage = signal(1);
  pageSize = signal(10);
  showFiltersPanel = signal(false);

  filterTabs: { id: 'all' | MarketType; label: string }[] = [
    { id: 'all', label: 'TOUT' },
    { id: 'CEX', label: 'CEX' },
    { id: 'DEX', label: 'DEX' },
  ];

  pairTypeTabs: { id: 'all' | PairType; label: string }[] = [
    { id: 'all', label: 'Tous' },
    { id: 'spot', label: 'Au comptant' },
    { id: 'perpetual', label: 'Perpétuel' },
    { id: 'futures', label: 'Contrats à terme' },
  ];

  filteredMarkets = computed(() => {
    let list = [...this.allMarkets];
    const typeFilter = this.activeFilter();
    const pairFilter = this.activePairFilter();
    if (typeFilter !== 'all') {
      list = list.filter((m) => m.type === typeFilter);
    }
    if (pairFilter !== 'all') {
      list = list.filter((m) => m.pairType === pairFilter);
    }
    return list;
  });

  totalItems = computed(() => this.filteredMarkets().length);

  paginatedMarkets = computed(() => {
    const list = this.filteredMarkets();
    const page = this.currentPage();
    const size = this.pageSize();
    const start = (page - 1) * size;
    return list.slice(start, start + size).map((row, i) => ({
      ...row,
      rank: start + i + 1,
    }));
  });

  totalPages = computed(() =>
    Math.max(1, Math.ceil(this.totalItems() / this.pageSize()))
  );

  /** Page numbers to show in pagination (with -1 for ellipsis). */
  pageNumbers = computed(() => {
    const total = this.totalPages();
    const current = this.currentPage();
    if (total <= 7) {
      return Array.from({ length: total }, (_, i) => i + 1);
    }
    const pages: number[] = [];
    pages.push(1);
    if (current > 3) pages.push(-1);
    for (let p = Math.max(2, current - 1); p <= Math.min(total - 1, current + 1); p++) {
      if (!pages.includes(p)) pages.push(p);
    }
    if (current < total - 2) pages.push(-1);
    if (total > 1) pages.push(total);
    return pages;
  });

  rangeStart = computed(
    () => (this.currentPage() - 1) * this.pageSize() + 1
  );
  rangeEnd = computed(() =>
    Math.min(
      this.currentPage() * this.pageSize(),
      this.totalItems()
    )
  );

  setFilter(id: 'all' | MarketType): void {
    this.activeFilter.set(id);
    this.currentPage.set(1);
  }

  setPairFilter(id: 'all' | PairType): void {
    this.activePairFilter.set(id);
    this.currentPage.set(1);
  }

  setPage(page: number): void {
    this.currentPage.set(Math.max(1, Math.min(page, this.totalPages())));
  }

  setPageSize(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(1);
  }

  toggleFiltersPanel(): void {
    this.showFiltersPanel.update((v) => !v);
  }
}
