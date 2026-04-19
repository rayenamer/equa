import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatCardComponent } from '../../components/molecules/stat-card/stat-card.component';
import { PriceChartComponent } from '../../components/price-chart/price-chart.component';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';
import { getAcmeAnnotations, getAcmeData } from '../../components/financial-chart/financial-chart.data';
import { ChartLinesStyleComponent } from '../../components/chart-lines-style/chart-lines-style.component';

import { ChartModule } from 'primeng/chart';

import { TransactionsTableComponent } from '../../components/transactions-table/transactions-table.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    StatCardComponent,
    PriceChartComponent,
    TransactionsTableComponent,
    UiButtonComponent,
    ChartLinesStyleComponent,
    ChartModule
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

  chartData = getAcmeData();
  chartTitle = 'Prix EQUA';
  chartAnnotations = getAcmeAnnotations();

  lineChartData: any;
  lineChartOptions: any;

  constructor() {
    this.initChart();
  }

  private initChart() {
    const data = this.chartData.slice(-30); // Last 30 points
    this.lineChartData = {
      labels: data.map(d => d.date.toLocaleDateString()),
      datasets: [
        {
          label: 'Prix EQUA',
          data: data.map(d => d.close),
          fill: true,
          borderColor: '#627eea',
          tension: 0.4,
          backgroundColor: 'rgba(98, 126, 234, 0.1)',
          pointRadius: 0
        }
      ]
    };

    this.lineChartOptions = {
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        x: {
          display: false
        },
        y: {
          grid: {
            color: 'rgba(255, 255, 255, 0.05)',
            drawBorder: false
          },
          ticks: {
            color: 'rgba(255, 255, 255, 0.5)',
            font: {
              size: 10
            }
          }
        }
      }
    };
  }
}
