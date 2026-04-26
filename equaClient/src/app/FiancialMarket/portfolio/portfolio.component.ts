import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-portfolio',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './portfolio.component.html',
    styleUrl: './portfolio.component.scss'
})
export class PortfolioComponent {
    portfolioStats = [
        { label: 'Valeur Totale', value: '1,540.20 DT', change: '+124.50 (8.1%)', isPos: true },
        { label: 'Profit/Perte', value: '+350.10 DT', change: 'Ce mois', isPos: true },
        { label: 'Actifs Détenus', value: '4', change: '2 secteurs', isPos: null }
    ];

    myAssets = [
        { name: 'EQUA Token', symbol: 'EQUA', balance: 120.5, value: 144.60, avgPrice: 1.15, profit: +6.05, color: '#627eea' },
        { name: 'Sousse Tech', symbol: 'STK', balance: 50, value: 615.00, avgPrice: 10.20, profit: +105.00, color: '#f1c40f' },
        { name: 'Olive Capital', symbol: 'OLV', balance: 10, value: 458.00, avgPrice: 47.00, profit: -12.00, color: '#27ae60' },
        { name: 'Sahara Solaris', symbol: 'SOL', balance: 500, value: 425.00, avgPrice: 0.80, profit: +25.00, color: '#e67e22' }
    ];
}
