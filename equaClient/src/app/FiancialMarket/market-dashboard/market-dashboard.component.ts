import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ChartModule } from 'primeng/chart';

@Component({
    selector: 'app-market-dashboard',
    standalone: true,
    imports: [CommonModule, RouterLink, ChartModule],
    templateUrl: './market-dashboard.component.html',
    styleUrl: './market-dashboard.component.scss'
})
export class MarketDashboardComponent {
    marketStats = [
        { label: 'Volume 24h', value: '4.2M TND', trend: '+12.5%', trendClass: 'trend-up' },
        { label: 'Market Cap', value: '128.5M TND', trend: '+2.1%', trendClass: 'trend-up' },
        { label: 'Actifs Listés', value: '42', trend: '+2', trendClass: 'trend-up' },
        { label: 'Utilisateurs', value: '1.2K', trend: '+85', trendClass: 'trend-up' }
    ];

    trendingAssets = [
        { name: 'EQUA-TND', symbol: 'EQTND', price: '1.20', change: '+5.4%', color: '#627eea' },
        { name: 'Olive Capital', symbol: 'OLV', price: '45.80', change: '-1.2%', color: '#27ae60' },
        { name: 'Sousse Tech', symbol: 'STK', price: '12.30', change: '+15.8%', color: '#f1c40f' }
    ];

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
}
