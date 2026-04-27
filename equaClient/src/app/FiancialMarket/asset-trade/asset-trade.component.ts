import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ChartModule } from 'primeng/chart';
import { FinancialMarketService } from '../financial-market.service';
import { AssetResponseFinancial, TradeRequestFinancial, PortfolioAssetResponseFinancial } from '../models/financial-market.models';

@Component({
    selector: 'app-asset-trade',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule, ChartModule],
    templateUrl: './asset-trade.component.html',
    styleUrl: './asset-trade.component.scss'
})
export class AssetTradeComponent implements OnInit {
    assetId: number = 0;
    tradeType: 'buy' | 'sell' = 'buy';
    amount: number = 0;
    currentBalance: number = 0;
    currentHoldingValue: number = 0;
    currentAssetHolding: PortfolioAssetResponseFinancial = {
        assetName: '',
        ticker: '',
        quantity: 0,
        avgBuyPriceEqua: 0,
        currentPriceEqua: 0,
        totalValueEqua: 0,
        pnlEqua: 0
    };
    assetDetails: AssetResponseFinancial | null = null;
    chartData: any;
    chartOptions: any;

    constructor(
        private route: ActivatedRoute,
        private financialService: FinancialMarketService
    ) { }

    ngOnInit(): void {
        const idParam = this.route.snapshot.paramMap.get('id');
        this.assetId = idParam ? parseInt(idParam) : 0;
        if (this.assetId) {
            this.loadAssetDetails();
            this.loadPriceHistory();
        }
        this.initChartOptions();
    }

    loadAssetDetails() {
        this.financialService.getAssetById(this.assetId).subscribe({
            next: (data) => {
                this.assetDetails = data;
                this.loadUserBalance(); // Fetch balance once we have asset details
            },
            error: (err) => console.error('Error loading asset details', err)
        });
    }

    loadUserBalance() {
        if (!this.assetDetails) return;

        this.financialService.getPortfolio().subscribe({
            next: (portfolio) => {
                const asset = portfolio.items.find(a => a.ticker === this.assetDetails?.ticker);
                if (asset) {
                    this.currentAssetHolding = asset;
                    this.currentBalance = asset.quantity;
                    this.currentHoldingValue = asset.totalValueEqua;
                } else {
                    // Create a default zero position
                    this.currentAssetHolding = {
                        assetName: this.assetDetails?.name || '',
                        ticker: this.assetDetails?.ticker || '',
                        quantity: 0,
                        avgBuyPriceEqua: 0,
                        currentPriceEqua: this.assetDetails?.currentPriceEqua || 0,
                        totalValueEqua: 0,
                        pnlEqua: 0
                    };
                    this.currentBalance = 0;
                    this.currentHoldingValue = 0;
                }
            },
            error: (err) => {
                console.error('Error loading balance', err);
            }
        });
    }

    loadPriceHistory() {
        this.financialService.getPriceHistory(this.assetId).subscribe({
            next: (history) => {
                const labels = history.map(h => {
                    const d = new Date(h.timestamp);
                    return isNaN(d.getTime()) ? 'Live' : d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
                });
                const prices = history.map(h => h.price);

                this.chartData = {
                    labels: labels.length > 0 ? labels : ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00'],
                    datasets: [
                        {
                            label: 'Prix',
                            data: prices.length > 0 ? prices : [1.18, 1.22, 1.21, 1.24, 1.23, 1.25, 1.20],
                            fill: true,
                            borderColor: '#627eea',
                            tension: 0.4,
                            backgroundColor: 'rgba(98, 126, 234, 0.1)',
                            pointRadius: 4,
                            pointBackgroundColor: '#627eea'
                        }
                    ]
                };
            },
            error: (err) => console.error('Error loading price history', err)
        });
    }

    setTradeType(type: 'buy' | 'sell') {
        this.tradeType = type;
    }

    estimatedQuantity() {
        if (!this.assetDetails || this.assetDetails.currentPriceEqua === 0) return 0;
        return this.amount / this.assetDetails.currentPriceEqua;
    }

    executeTrade() {
        if (this.amount <= 0 || !this.assetDetails) return;

        const req: TradeRequestFinancial = {
            assetId: this.assetId,
            amountEqua: this.amount
        };

        const tradeObservable = this.tradeType === 'buy'
            ? this.financialService.buyAsset(req)
            : this.financialService.sellAsset(req);

        tradeObservable.subscribe({
            next: (res) => {
                alert(`Transaction réussie !\nID: ${res.transactionId}\nQuantité: ${res.quantity}\nTotal: ${res.totalEqua} Equa`);
                this.loadAssetDetails();
                this.loadUserBalance();
            },
            error: (err) => {
                console.error('Trade error', err);
                const errorMsg = err.error?.message || 'Une erreur est survenue lors de la transaction.';
                alert(`Erreur: ${errorMsg}`);
            }
        });
    }

    initChartOptions() {
        this.chartOptions = {
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                x: {
                    grid: { display: false },
                    ticks: { color: '#94a3b8' }
                },
                y: {
                    grid: { color: 'rgba(255, 255, 255, 0.05)' },
                    ticks: { color: '#94a3b8' }
                }
            }
        };
    }
}
