import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ChartModule } from 'primeng/chart';
import { FinancialMarketService } from '../financial-market.service';
import { AssetResponseFinancial, AssetMarketSummaryResponse, PriceHistoryResponseFinancial } from '../models/financial-market.models';

@Component({
    selector: 'app-market-dashboard',
    standalone: true,
    imports: [CommonModule, RouterLink, ChartModule],
    templateUrl: './market-dashboard.component.html',
    styleUrl: './market-dashboard.component.scss'
})
export class MarketDashboardComponent implements OnInit {
    marketStats: any[] = [];

    trendingAssets: AssetResponseFinancial[] = [];

    chartData = {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
        datasets: [
            {
                label: 'Volume Global',
                data: [65, 59, 80, 81, 56, 95],
                fill: true,
                borderColor: '#9b59b6',
                tension: 0.4,
                backgroundColor: 'rgba(155, 89, 182, 0.1)'
            }
        ]
    };

    chartOptions = {
        maintainAspectRatio: false,
        plugins: {
            legend: { display: false },
            tooltip: {
                enabled: true,
                mode: 'index',
                intersect: false,
                callbacks: {
                    label: (context: any) => {
                        return `Prix: ${context.parsed.y.toFixed(2)} Equa`;
                    }
                }
            }
        },
        interaction: {
            mode: 'nearest',
            axis: 'x',
            intersect: false
        },
        scales: {
            x: {
                display: false,
                grid: { display: false }
            },
            y: {
                display: false,
                grid: { display: false }
            }
        }
    };

    constructor(private financialService: FinancialMarketService) { }

    ngOnInit(): void {
        this.loadTrendingAssets();
        this.loadMarketSummary();
        this.loadAggregatedHistory();
    }

    loadMarketSummary() {
        this.financialService.getMarketSummary().subscribe({
            next: (summary) => {
                this.marketStats = [
                    {
                        label: 'Market Cap Total',
                        value: `${summary.totalMarketCap.toLocaleString()} Equa`,
                        trend: 'Global',
                        trendClass: 'trend-up'
                    },
                    {
                        label: 'Volume 24h',
                        value: `${summary.totalVolume24h.toLocaleString()} Equa`,
                        trend: 'Actif',
                        trendClass: 'trend-up'
                    },
                    {
                        label: 'Actifs Listés',
                        value: summary.assetCount.toString(),
                        trend: 'Vérifiés',
                        trendClass: 'trend-up'
                    }
                ];
            },
            error: (err) => console.error('Error fetching market summary', err)
        });
    }

    loadAggregatedHistory() {
        this.financialService.getAggregatedPriceHistory().subscribe({
            next: (history) => {
                if (history && history.length > 0) {
                    this.chartData = {
                        labels: history.map(h => new Date(h.recordedAt).toLocaleDateString()),
                        datasets: [
                            {
                                label: 'Prix Moyen Global (Equa)',
                                data: history.map(h => h.priceEqua),
                                fill: true,
                                borderColor: '#9b59b6',
                                tension: 0.4,
                                backgroundColor: 'rgba(155, 89, 182, 0.1)'
                            }
                        ]
                    };
                }
            },
            error: (err) => console.error('Error fetching aggregated history', err)
        });
    }

    loadTrendingAssets() {
        this.financialService.getAllAssets().subscribe({
            next: (data) => {
                // Just take the first 3 for trending
                this.trendingAssets = data.slice(0, 3);
            },
            error: (err) => console.error('Error fetching trending assets', err)
        });
    }
}
