import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ChartModule } from 'primeng/chart';
import { FinancialMarketService } from '../financial-market.service';
import { AssetResponseFinancial } from '../models/financial-market.models';

@Component({
    selector: 'app-market-dashboard',
    standalone: true,
    imports: [CommonModule, RouterLink, ChartModule],
    templateUrl: './market-dashboard.component.html',
    styleUrl: './market-dashboard.component.scss'
})
export class MarketDashboardComponent implements OnInit {
    marketStats = [
        { label: 'Volume 24h', value: '4.2M TND', trend: '+12.5%', trendClass: 'trend-up' },
        { label: 'Market Cap', value: '128.5M TND', trend: '+2.1%', trendClass: 'trend-up' },
        { label: 'Actifs Listés', value: '42', trend: '+2', trendClass: 'trend-up' },
        { label: 'Utilisateurs', value: '1.2K', trend: '+85', trendClass: 'trend-up' }
    ];

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
            legend: { display: false }
        },
        scales: {
            x: { display: false },
            y: { display: false }
        }
    };

    constructor(private financialService: FinancialMarketService) { }

    ngOnInit(): void {
        this.loadTrendingAssets();
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
