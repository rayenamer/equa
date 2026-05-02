import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChartModule } from 'primeng/chart';
import { ApiService } from '../../services/api.service';
import { WalletDTO, DinarWallet } from '../../BlockChain/models/dinar-wallet.model';
import { gsap } from 'gsap';

@Component({
  selector: 'app-token-details',
  standalone: true,
  imports: [CommonModule, FormsModule, ChartModule],
  templateUrl: './token-details.component.html',
  styleUrl: './token-details.component.scss'
})
export class TokenDetailsComponent implements OnInit, OnDestroy {
  priceData: any;
  tokenomicsData: any;
  chartOptions: any;
  pieOptions: any;

  currentPrice: number = 3.35;
  priceInterval: any;

  equaWallet: WalletDTO | null = null;
  dinarWallet: DinarWallet | null = null;

  swapAmountPay: number | null = null;
  swapAmountReceive: number | null = null;
  isSwapping = false;
  isCreatingEqua = false;
  isCreatingDinar = false;

  activeFilter: string = '1W';

  toastMessage: string | null = null;
  toastType: 'success' | 'error' | 'info' = 'info';

  stats = [
    { label: 'Circulating Supply', val: '118,500,000 EQUA', icon: 'pi-chart-pie' },
    { label: 'Total Supply', val: '120,000,000 EQUA', icon: 'pi-database' },
    { label: 'Market Cap', val: '396,975,000 TND', icon: 'pi-chart-line' },
    { label: 'Tokens Burned', val: '1,500,000 EQUA', icon: 'pi-fire' }
  ];

  constructor(private apiService: ApiService) { }

  ngOnInit() {
    this.initCharts();
    this.loadData();
    this.animate();

    // Simulate backend's applyJitter for live UI updates
    this.priceInterval = setInterval(() => {
      const jitter = (Math.random() - 0.5) * 0.02;
      this.currentPrice = Number((this.currentPrice + jitter).toFixed(3));
      this.onPayChange();
    }, 3500);
  }

  ngOnDestroy() {
    if (this.priceInterval) clearInterval(this.priceInterval);
  }

  showToast(message: string, type: 'success' | 'error' | 'info' = 'info') {
    this.toastMessage = message;
    this.toastType = type;
    setTimeout(() => this.toastMessage = null, 4000);
  }

  loadData() {
    this.apiService.getMyWallet().subscribe({ next: (w) => this.equaWallet = w, error: () => this.equaWallet = null });
    this.apiService.getMyDinarWallet().subscribe({ next: (w) => this.dinarWallet = w, error: () => this.dinarWallet = null });
    this.apiService.getCurrentRate().subscribe({ next: (rate) => this.currentPrice = rate, error: () => { } });
  }

  setFilter(filter: string) {
    this.activeFilter = filter;
    let newData = [];
    // Simulate chart data change depending on filter
    switch (filter) {
      case '1H': newData = [3.30, 3.31, 3.32, 3.33, 3.35, 3.34, this.currentPrice]; break;
      case '1D': newData = [3.20, 3.25, 3.30, 3.28, 3.35, 3.40, this.currentPrice]; break;
      case '1M': newData = [2.80, 2.95, 3.10, 3.05, 3.20, 3.30, this.currentPrice]; break;
      case '1Y': newData = [1.50, 1.80, 2.20, 2.50, 2.90, 3.10, this.currentPrice]; break;
      default: newData = [3.20, 3.25, 3.22, 3.28, 3.30, 3.32, this.currentPrice]; break;
    }

    this.priceData = { ...this.priceData };
    this.priceData.datasets[0].data = newData;
  }

  createEquaWallet() {
    this.isCreatingEqua = true;
    this.apiService.createWallet().subscribe({
      next: (wallet) => { this.equaWallet = wallet; this.isCreatingEqua = false; this.showToast('Portefeuille EQUA activé!', 'success'); },
      error: (err) => { this.showToast('Erreur création EQUA: ' + err.message, 'error'); this.isCreatingEqua = false; }
    });
  }

  createDinarWallet() {
    this.isCreatingDinar = true;
    this.apiService.createDinarWallet().subscribe({
      next: (wallet) => { this.dinarWallet = wallet; this.isCreatingDinar = false; this.showToast('Portefeuille TND activé!', 'success'); },
      error: (err) => { this.showToast('Erreur création Dinar: ' + err.message, 'error'); this.isCreatingDinar = false; }
    });
  }

  onPayChange() {
    if (this.swapAmountPay) {
      const fee = this.swapAmountPay * 0.005;
      this.swapAmountReceive = (this.swapAmountPay - fee) / this.currentPrice;
    } else {
      this.swapAmountReceive = null;
    }
  }

  setMax() {
    if (this.dinarWallet) {
      this.swapAmountPay = Math.floor(this.dinarWallet.balance);
      this.onPayChange();
    }
  }

  isInsufficientBalance(): boolean {
    if (!this.dinarWallet || !this.swapAmountPay) return false;
    return this.dinarWallet.balance < this.swapAmountPay;
  }

  executeSwap() {
    if (!this.equaWallet || this.equaWallet.status === 'SUSPENDED' || this.equaWallet.status === 'INACTIVE') {
      this.showToast("Votre portefeuille EQUA doit être ACTIF pour cette opération.", 'error');
      return;
    }
    if (!this.dinarWallet) { this.showToast("Activez votre portefeuille TND.", 'info'); return; }

    const amountToConvert = Math.floor(this.swapAmountPay || 0);

    if (amountToConvert <= 0) { this.showToast('Entrez un montant entier (ex: 6)', 'error'); return; }

    // Règle Métier #1 : Transaction Max 50 000
    if (amountToConvert > 50000) { this.showToast('Limite dépassée : Transaction maximale de 50 000.', 'error'); return; }

    if (this.dinarWallet.balance < amountToConvert) { this.showToast('Solde TND insuffisant', 'error'); return; }

    this.isSwapping = true;
    this.apiService.convertDinarsToEqua(amountToConvert).subscribe({
      next: (wallet) => {
        this.equaWallet = wallet;
        this.apiService.getMyDinarWallet().subscribe(w => this.dinarWallet = w);
        this.swapAmountPay = null;
        this.swapAmountReceive = null;
        this.isSwapping = false;
        this.showToast(`Swap de ${amountToConvert} TND exécuté avec succès!`, 'success');
      },
      error: (err) => {
        this.isSwapping = false;
        this.showToast('Erreur lors du swap: ' + (err.error?.message || err.message), 'error');
      }
    });
  }

  initCharts() {
    this.priceData = {
      labels: ['10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00'],
      datasets: [
        {
          label: 'EQUA/TND',
          data: [3.20, 3.25, 3.22, 3.28, 3.30, 3.32, 3.35],
          borderColor: '#00d4ff',
          tension: 0.4,
          fill: true,
          backgroundColor: 'rgba(0, 212, 255, 0.1)',
          pointRadius: 0,
          pointHitRadius: 10
        }
      ]
    };

    this.tokenomicsData = {
      labels: ['Public Sale', 'Reserve', 'Ecosystem', 'Team'],
      datasets: [
        {
          data: [60, 20, 15, 5],
          backgroundColor: ['#FFD700', '#00d4ff', '#a78bfa', '#34d399'],
          hoverBackgroundColor: ['#FFD700', '#00d4ff', '#a78bfa', '#34d399'],
          borderWidth: 0
        }
      ]
    };

    this.chartOptions = {
      plugins: { legend: { display: false } },
      scales: {
        x: { ticks: { color: '#808080' }, grid: { color: 'rgba(255,255,255,0.02)' } },
        y: { ticks: { color: '#808080' }, grid: { color: 'rgba(255,255,255,0.02)' } }
      },
      maintainAspectRatio: false
    };

    this.pieOptions = {
      plugins: { legend: { display: false } }, cutout: '75%', maintainAspectRatio: false
    };
  }

  animate() { setTimeout(() => { gsap.from('.token-anim', { y: 30, opacity: 0, duration: 0.8, stagger: 0.15, ease: 'power3.out' }); }, 100); }
}
