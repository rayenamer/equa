import { Component, OnInit, OnDestroy, signal, NgZone, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatCardComponent } from '../../components/molecules/stat-card/stat-card.component';
import { PriceChartComponent } from '../../components/price-chart/price-chart.component';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';
import { ChartLinesStyleComponent } from '../../components/chart-lines-style/chart-lines-style.component';
import { ChartModule } from 'primeng/chart';
import { TransactionsTableComponent } from '../../components/transactions-table/transactions-table.component';
import { CompactBalanceComponent } from '../../components/compact-balance/compact-balance.component';
import { ApiService } from '../../services/api.service';
import { TransactionService } from '../services/transaction.service';
import { DinarWallet, WalletDTO } from '../models/dinar-wallet.model';
import { Transaction } from '../models/transaction.model';
import { forkJoin } from 'rxjs';

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
    ChartModule,
    CompactBalanceComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit, OnDestroy {
  dinarWallet = signal<DinarWallet | null>(null);
  equaWallet = signal<WalletDTO | null>(null);
  recentTransactions = signal<Transaction[]>([]);
  currentRate = signal<number>(1.0);

  dinarsPerEqua = computed(() =>
    this.currentRate() > 0 ? 1 / this.currentRate() : 0
  );

  private readonly CHART_STORAGE_KEY = 'equa_price_history';

  stats = [
    { label: 'Dernier Bloc', value: '#2,456,891' },
    { label: 'Nœuds Actifs', value: '128' },
    { label: 'TPS Actuel', value: '45.2' },
    { label: 'Santé Réseau', value: '99.8%' }
  ];

  lineChartData: any;
  lineChartOptions: any;
  private eventSource?: EventSource;

  constructor(
    private apiService: ApiService,
    private transactionService: TransactionService,
    private ngZone: NgZone
  ) {
    this.initChart();
  }

  ngOnInit(): void {
    this.loadDashboardData();
    this.subscribeToRates();
    this.getCurrentRate();
  }

  ngOnDestroy(): void {
    if (this.eventSource) {
      this.eventSource.close();
    }
  }

  private subscribeToRates() {
    this.eventSource = new EventSource('http://localhost:8081/api/v1/rates/stream');

    this.eventSource.onmessage = (event) => {
      this.ngZone.run(() => {
        const rate = parseFloat(event.data);
        if (!isNaN(rate)) {
          this.currentRate.set(rate);
          this.updateChartData(rate);
        }
      });
    };

    this.eventSource.onerror = (error) => {
      console.error('SSE Error:', error);
      this.eventSource?.close();
    };
  }

  private updateChartData(rate: number) {
    const labels = [...(this.lineChartData?.labels || [])];
    const data = [...(this.lineChartData?.datasets[0]?.data || [])];

    labels.push(new Date().toLocaleTimeString());
    data.push(rate);

    // Keep last 100 points
    if (labels.length > 100) {
      labels.shift();
      data.shift();
    }

    this.saveChartHistory(labels, data);

    this.lineChartData = {
      labels: labels,
      datasets: [
        {
          label: 'EQUA/Dinar',
          data: data,
          fill: true,
          borderColor: '#627eea',
          backgroundColor: 'rgba(98, 126, 234, 0.1)',
          tension: 0.4,
          pointRadius: 0
        }
      ]
    };
  }

  private saveChartHistory(labels: string[], data: number[]) {
    try {
      localStorage.setItem(this.CHART_STORAGE_KEY, JSON.stringify({ labels, data }));
    } catch (e) {
      console.warn('Failed to save chart history', e);
    }
  }

  private loadDashboardData(): void {
    forkJoin({
      dinar: this.apiService.getMyDinarWallet(),
      equa: this.apiService.getMyWallet(),
      transactions: this.transactionService.getAllTransactions()
    }).subscribe({
      next: (data) => {
        this.dinarWallet.set(data.dinar);
        this.equaWallet.set(data.equa);
        this.recentTransactions.set(data.transactions?.slice(0, 5) || []);
      },
      error: (err) => console.error('Error loading dashboard data:', err)
    });
  }

  private initChart() {
    let initialLabels = [];
    let initialData = [];

    try {
      const saved = localStorage.getItem(this.CHART_STORAGE_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        initialLabels = parsed.labels;
        initialData = parsed.data;
      }
    } catch (e) {
      console.warn('Failed to load chart history', e);
    }

    if (initialLabels.length === 0) {
      initialLabels = [new Date(Date.now() - 1000).toLocaleTimeString()];
      initialData = [1.0];
    }

    this.lineChartData = {
      labels: initialLabels,
      datasets: [
        {
          label: 'EQUA/Dinar',
          data: initialData,
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
        legend: { display: false },
        tooltip: { mode: 'index', intersect: false }
      },
      animation: {
        duration: 0
      },
      scales: {
        x: { display: false },
        y: {
          min: 0,
          suggestedMax: 2,
          grid: { color: 'rgba(255, 255, 255, 0.05)', drawBorder: false },
          ticks: { color: 'rgba(255, 255, 255, 0.5)', font: { size: 10 } }
        }
      }
    };
  }

  private getCurrentRate() {
    this.apiService.getCurrentRate().subscribe({
      next: (rate) => {
        this.currentRate.set(rate);
        this.updateChartData(rate);
      },
      error: (err) => console.error('Error loading current rate:', err)
    });
  }
}
