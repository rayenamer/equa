import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatCardComponent } from '../../components/molecules/stat-card/stat-card.component';
import { PriceChartComponent } from '../../components/price-chart/price-chart.component';
import { MarketsTableComponent } from '../../components/markets-table/markets-table.component';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    StatCardComponent,
    PriceChartComponent,
    MarketsTableComponent,
    UiButtonComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {
  stats = [
    { label: 'Dernier Bloc', value: '#2,456,891' },
    { label: 'Nœuds Actifs', value: '128' },
    { label: 'TPS Actuel', value: '45.2' },
    { label: 'Santé Réseau', value: '99.8%' }
  ];
}
