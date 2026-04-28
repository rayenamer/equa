import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FinancialMarketService } from '../financial-market.service';
import { PortfolioResponseFinancial, TransactionResponseFinancial } from '../models/financial-market.models';

@Component({
    selector: 'app-portfolio',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './portfolio.component.html',
    styleUrl: './portfolio.component.scss'
})
export class PortfolioComponent implements OnInit {
    portfolio: PortfolioResponseFinancial | null = null;
    transactions: TransactionResponseFinancial[] = [];

    constructor(private financialService: FinancialMarketService) { }

    ngOnInit(): void {
        this.loadPortfolio();
        this.loadTransactions();
    }

    loadPortfolio() {
        this.financialService.getPortfolio().subscribe({
            next: (data) => {
                this.portfolio = data;
            },
            error: (err) => console.error('Error fetching portfolio', err)
        });
    }

    loadTransactions() {
        this.financialService.getTransactions().subscribe({
            next: (data) => {
                this.transactions = data;
            },
            error: (err) => console.error('Error fetching transactions', err)
        });
    }

    get portfolioStats() {
        if (!this.portfolio) return [];

        const totalPnL = this.portfolio.items.reduce((sum, item) => sum + item.pnlEqua, 0);
        const initialValue = this.portfolio.totalValueEqua - totalPnL;
        const pnlPercentage = initialValue !== 0 ? (totalPnL / initialValue) * 100 : 0;

        return [
            { label: 'Valeur Totale', value: `${this.portfolio.totalValueEqua.toFixed(2)} Equa`, change: `${totalPnL >= 0 ? '+' : ''}${totalPnL.toFixed(2)} (${pnlPercentage.toFixed(1)}%)`, isPos: totalPnL >= 0 },
            { label: 'Profit/Perte', value: `${totalPnL >= 0 ? '+' : ''}${totalPnL.toFixed(2)} Equa`, change: 'Total', isPos: totalPnL >= 0 },
            { label: 'Actifs Détenus', value: this.portfolio.items.length.toString(), change: 'Secteurs variés', isPos: null }
        ];
    }
}
