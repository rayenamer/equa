import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatCardComponent } from '../../components/molecules/stat-card/stat-card.component';
import { PriceChartComponent } from '../../components/price-chart/price-chart.component';
import { TransactionsTableComponent } from '../../components/transactions-table/transactions-table.component';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';

@Component({
  selector: 'app-wallet',
  standalone: true,
  imports: [
    CommonModule,
    StatCardComponent,
    PriceChartComponent,
    TransactionsTableComponent,
    UiButtonComponent
  ],
  templateUrl: './wallet.component.html',
  styleUrl: './wallet.component.scss'
})
export class WalletComponent {
  walletStats = [
    { label: 'Solde Dinar', value: '45,280.00' },
    { label: 'Valeur en EUR', value: '€45,280.00' },
    { label: 'Transactions (Mois)', value: '24' },
    { label: 'Frais Payés', value: '1.2 DIN' }
  ];
}
