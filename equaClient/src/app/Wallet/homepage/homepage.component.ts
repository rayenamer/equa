import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { WalletDTO, DinarWallet } from '../../BlockChain/models/dinar-wallet.model';
import { ChartModule } from 'primeng/chart';
import { gsap } from 'gsap';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [CommonModule, FormsModule, ChartModule],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.scss'
})
export class HomepageComponent implements OnInit, OnDestroy {
  equaWallet: WalletDTO | null = null;
  dinarWallet: DinarWallet | null = null;
  fraudRisk: string = 'LOW';
  loading = true;
  isRefreshing = false;

  chartData: any;
  chartOptions: any;

  currentRate: number = 3.35; // 1 EQUA = 3.35 TND fallback

  payAmount: number | null = null;
  receiveAmount: number | null = null;
  isConverting = false;
  isCreatingEqua = false;
  isCreatingDinar = false;

  // Custom Toast State
  toastMessage: string | null = null;
  toastType: 'success' | 'error' | 'info' = 'info';

  // Modal State
  activeModal: 'send' | 'receive' | null = null;
  sendRecipientId: number | null = null;
  sendAmount: number | null = null;
  isSending = false;

  recentTransactions = [
    { type: 'receive', title: 'System Initialized', date: 'Welcome', amount: '0.00' }
  ];

  priceInterval: any;

  constructor(private apiService: ApiService) { }

  ngOnInit() {
    this.loadData();
    this.initChart();

    // Simulate live market price jitter
    this.priceInterval = setInterval(() => {
      const jitter = (Math.random() - 0.5) * 0.02;
      this.currentRate = Number((this.currentRate + jitter).toFixed(3));
      this.onPayChange();
    }, 4000);
  }

  ngOnDestroy() {
    if (this.priceInterval) clearInterval(this.priceInterval);
  }

  showToast(message: string, type: 'success' | 'error' | 'info' = 'info') {
    this.toastMessage = message;
    this.toastType = type;
    setTimeout(() => this.toastMessage = null, 4000);
  }

  refreshData() {
    this.isRefreshing = true;
    this.loadData();
    setTimeout(() => this.isRefreshing = false, 1000);
  }

  loadData() {
    this.apiService.getMyWallet().subscribe({
      next: (wallet) => {
        this.equaWallet = wallet;
        if (wallet) {
          this.fraudRisk = wallet.fraudRiskLevel || 'LOW';
          const history = (wallet as any).transactionHistory;
          if (history && Array.isArray(history) && history.length > 0) {
            this.recentTransactions = this.parseHistory(history);
          } else {
            this.recentTransactions = [
              { type: 'receive', title: 'Wallet Activated', date: 'Just now', amount: '+ 0.00 EQUA' }
            ];
          }
        }
        this.loading = false;
        if (!this.isRefreshing) this.animateIn();
      },
      error: () => {
        this.equaWallet = null;
        this.loading = false;
        if (!this.isRefreshing) this.animateIn();
      }
    });

    this.apiService.getMyDinarWallet().subscribe({
      next: (wallet) => this.dinarWallet = wallet,
      error: (err) => this.dinarWallet = null
    });

    this.apiService.getCurrentRate().subscribe({
      next: (rate) => this.currentRate = rate,
      error: () => { }
    });
  }

  parseHistory(history: string[]) {
    return history.map(h => {
      const parts = h.split('|');
      const type = parts[0] ? parts[0].trim().toLowerCase() : 'unknown';
      const amount = parts[1] ? parts[1].trim() : '';

      let iconType = 'swap';
      if (type.includes('deposit') || type.includes('receive')) iconType = 'receive';
      if (type.includes('withdraw') || type.includes('send')) iconType = 'send';

      return {
        type: iconType,
        title: parts[0] ? parts[0].trim() : 'Transaction',
        date: 'Recent',
        amount: amount
      };
    }).reverse().slice(0, 5);
  }

  createEquaWallet() {
    this.isCreatingEqua = true;
    this.apiService.createWallet().subscribe({
      next: (wallet) => {
        this.equaWallet = wallet;
        this.isCreatingEqua = false;
        this.showToast('Portefeuille EQUA activé avec succès!', 'success');
        this.loadData();
      },
      error: (err) => {
        this.showToast('Erreur création EQUA: ' + err.message, 'error');
        this.isCreatingEqua = false;
      }
    });
  }

  createDinarWallet() {
    this.isCreatingDinar = true;
    this.apiService.createDinarWallet().subscribe({
      next: (wallet) => {
        this.dinarWallet = wallet;
        this.isCreatingDinar = false;
        this.showToast('Portefeuille TND activé avec succès!', 'success');
      },
      error: (err) => {
        this.showToast('Erreur création Dinar: ' + err.message, 'error');
        this.isCreatingDinar = false;
      }
    });
  }

  onPayChange() {
    if (this.payAmount) {
      const fee = this.payAmount * 0.005;
      this.receiveAmount = (this.payAmount - fee) / this.currentRate;
    } else {
      this.receiveAmount = null;
    }
  }

  isInsufficientBalance(): boolean {
    if (!this.dinarWallet || !this.payAmount) return false;
    return this.dinarWallet.balance < this.payAmount;
  }

  convertDinars() {
    if (!this.equaWallet || this.equaWallet.status === 'SUSPENDED' || this.equaWallet.status === 'INACTIVE') {
      this.showToast("Votre portefeuille EQUA doit être ACTIF pour cette opération.", 'error');
      return;
    }
    if (!this.dinarWallet) { this.showToast("Activez votre portefeuille Dinar.", 'info'); return; }

    const amountToConvert = Math.floor(this.payAmount || 0);

    if (amountToConvert <= 0) { this.showToast('Entrez un montant entier (ex: 6)', 'error'); return; }

    // Règle Métier #1 : Transaction Max 50 000
    if (amountToConvert > 50000) { this.showToast('Limite dépassée : Transaction maximale de 50 000.', 'error'); return; }

    if (this.dinarWallet.balance < amountToConvert) { this.showToast('Solde TND insuffisant', 'error'); return; }

    this.isConverting = true;
    this.apiService.convertDinarsToEqua(amountToConvert).subscribe({
      next: (wallet) => {
        this.equaWallet = wallet;
        this.apiService.getMyDinarWallet().subscribe(w => this.dinarWallet = w);
        this.showToast(`Conversion réussie!`, 'success');

        const fee = amountToConvert * 0.005;
        this.recentTransactions.unshift({
          type: 'swap',
          title: 'CONVERT (TND to EQUA)',
          date: 'À l\'instant',
          amount: `+ ${((amountToConvert - fee) / this.currentRate).toFixed(4)} EQUA`
        });

        this.payAmount = null;
        this.receiveAmount = null;
        this.isConverting = false;
      },
      error: (err) => {
        this.isConverting = false;
        this.showToast('Erreur: ' + (err.error?.message || err.message), 'error');
      }
    });
  }

  // --- Modals Logic ---
  openModal(type: 'send' | 'receive') { this.activeModal = type; }
  closeModal(event?: Event) { if (event) event.stopPropagation(); this.activeModal = null; this.sendAmount = null; this.sendRecipientId = null; }

  copyAddress() {
    if (this.equaWallet?.publicKey) {
      navigator.clipboard.writeText(this.equaWallet.publicKey);
      this.showToast('Clé publique copiée au presse-papier !', 'success');
    } else {
      this.showToast('Clé publique introuvable', 'error');
    }
  }

  executeSend() {
    if (!this.equaWallet || this.equaWallet.status === 'SUSPENDED' || this.equaWallet.status === 'INACTIVE') {
      this.showToast('Votre portefeuille doit être ACTIF pour effectuer un transfert.', 'error'); return;
    }
    if (!this.equaWallet?.id || !this.sendRecipientId || !this.sendAmount || this.sendAmount <= 0) {
      this.showToast('Veuillez remplir correctement les informations.', 'error'); return;
    }

    // Règle Métier #1 : Transaction max 50 000
    if (this.sendAmount > 50000) {
      this.showToast('Transaction rejetée : Maximum autorisé 50 000 EQUA.', 'error'); return;
    }

    if (this.sendAmount > (this.equaWallet?.equaAmount || 0)) {
      this.showToast('Solde EQUA insuffisant.', 'error'); return;
    }

    // Règle Métier #2 : Solde minimum 10 après retrait/transfert
    if ((this.equaWallet.equaAmount - this.sendAmount) < 10) {
      this.showToast('Le solde ne peut pas descendre sous 10 EQUA après transfert.', 'error'); return;
    }

    this.isSending = true;
    // Simulate Smart Contract execution delay
    setTimeout(() => {
      this.isSending = false;
      this.showToast(`Transfert de ${this.sendAmount} EQUA confirmé sur le réseau.`, 'success');
      this.recentTransactions.unshift({
        type: 'send', title: `Sent to #${this.sendRecipientId}`, date: 'À l\'instant', amount: `- ${this.sendAmount} EQUA`
      });
      if (this.equaWallet) this.equaWallet.equaAmount -= this.sendAmount!;
      this.closeModal();
    }, 1500);
  }

  scrollToConvert() {
    document.querySelector('.convert-card')?.scrollIntoView({ behavior: 'smooth', block: 'center' });
    setTimeout(() => {
      gsap.fromTo('.convert-card', { boxShadow: '0 0 50px rgba(255,215,0,0.8)' }, { boxShadow: '0 10px 30px rgba(0,0,0,0.5)', duration: 1.5 });
    }, 500);
  }

  getTierColor(): string {
    const tier = this.equaWallet?.loyaltyTier || 'BRONZE';
    switch (tier) {
      case 'PLATINUM': return '#E5E4E2';
      case 'GOLD': return '#FFD700';
      case 'SILVER': return '#C0C0C0';
      default: return '#cd7f32';
    }
  }

  initChart() {
    this.chartData = {
      labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
      datasets: [{
        label: 'EQUA Balance', data: [1200, 1500, 1400, 1800, 2200, 2100, 2450], fill: true, borderColor: '#FFD700', tension: 0.4, backgroundColor: 'rgba(255, 215, 0, 0.1)', pointBackgroundColor: '#FFD700', pointBorderColor: '#1a1a2e', pointBorderWidth: 2, pointRadius: 4, pointHoverRadius: 6
      }]
    };
    this.chartOptions = { plugins: { legend: { display: false }, tooltip: { backgroundColor: 'rgba(10, 10, 18, 0.9)', titleColor: '#FFD700', bodyColor: '#fff', padding: 10, displayColors: false, cornerRadius: 8 } }, scales: { x: { display: false }, y: { display: false } }, maintainAspectRatio: false };
  }

  getTxIcon(type: string): string {
    switch (type) { case 'receive': return 'pi-arrow-down-left'; case 'send': return 'pi-arrow-up-right'; case 'swap': return 'pi-sync'; default: return 'pi-circle-fill'; }
  }

  animateIn() { setTimeout(() => { gsap.from('.wallet-card-anim', { y: 40, opacity: 0, duration: 0.8, stagger: 0.15, ease: 'power3.out' }); }, 100); }
}
