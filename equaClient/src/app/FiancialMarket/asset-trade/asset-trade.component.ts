import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ChartModule } from 'primeng/chart';

@Component({
    selector: 'app-asset-trade',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule, ChartModule],
    templateUrl: './asset-trade.component.html',
    styleUrl: './asset-trade.component.scss'
})
export class AssetTradeComponent implements OnInit {
    assetSymbol: string = '';
    tradeType: 'buy' | 'sell' = 'buy';
    amount: number = 0;

    assetDetails = {
        name: 'EQUA Token',
        symbol: 'EQUA',
        price: 1.20,
        change: +2.5,
        high: 1.25,
        low: 1.18,
        marketCap: '1.2M',
        volume: '450K',
        description: 'Le jeton utilitaire central de l\'écosystème EQUA, utilisé pour les transactions et la gouvernance.'
    };

    chartData: any;
    chartOptions: any;

    constructor(private route: ActivatedRoute) { }

    ngOnInit(): void {
        this.assetSymbol = this.route.snapshot.paramMap.get('id') || 'EQUA';
        this.initChart();
    }

    setTradeType(type: 'buy' | 'sell') {
        this.tradeType = type;
    }

    totalValue() {
        return this.amount * this.assetDetails.price;
    }

    initChart() {
        this.chartData = {
            labels: ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00'],
            datasets: [
                {
                    label: 'Prix',
                    data: [1.18, 1.22, 1.21, 1.24, 1.23, 1.25, 1.20],
                    fill: true,
                    borderColor: '#627eea',
                    tension: 0.4,
                    backgroundColor: 'rgba(98, 126, 234, 0.1)',
                    pointRadius: 4,
                    pointBackgroundColor: '#627eea'
                }
            ]
        };

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
