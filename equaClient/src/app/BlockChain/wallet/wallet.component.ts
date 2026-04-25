import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DinarBalanceCardComponent } from '../../components/dinar-balance-card/dinar-balance-card.component';
import { DinarActionsComponent } from '../../components/dinar-actions/dinar-actions.component';
import { DinarInventoryComponent } from '../../components/dinar-inventory/dinar-inventory.component';
import { ApiService } from '../../services/api.service';
import { DinarWallet,WalletDTO } from '../models/dinar-wallet.model';
import { catchError, of } from 'rxjs';
import { AuthService } from '../../User/services/auth.service';

@Component({
  selector: 'app-wallet',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DinarBalanceCardComponent,
    DinarActionsComponent,
    DinarInventoryComponent
  ],
  templateUrl: './wallet.component.html',
  styleUrl: './wallet.component.scss'
})
export class WalletComponent implements OnInit {
  dinarWallet: DinarWallet | null = null;
  equaWallet: WalletDTO | null = null;
  dinarWalletExists = false;
  equaWalletExists = false;
  convertAmount = 0;

  constructor(private apiService: ApiService, private authService: AuthService) {}

  ngOnInit() {
    this.loadWallets();
  }

  loadWallets() {
    this.apiService.getMyDinarWallet().pipe(
      catchError(() => of(null))
    ).subscribe(wallet => {
      this.dinarWallet = wallet;
      this.dinarWalletExists = !!wallet;
    });

    this.apiService.getMyWallet().pipe(
      catchError(() => of(null))
    ).subscribe(wallet => {
      this.equaWallet = wallet;
      this.equaWalletExists = !!wallet;
    });
  }

  createDinarWallet() {
    this.apiService.createDinarWallet().subscribe(wallet => {
      this.dinarWallet = wallet;
      this.dinarWalletExists = true;
    });
  }

  createEquaWallet() {
    this.apiService.createWallet().subscribe(wallet => {
      this.equaWallet = wallet;
      this.equaWalletExists = true;
    });
  }

  handleWalletAction(event: { type: 'deposit' | 'withdraw', amount: number }) {
    if (!this.dinarWallet) return;

    const action = event.type === 'deposit' ? this.apiService.depositDinar(this.dinarWallet.walletId, event.amount) : this.apiService.withdrawDinar(this.dinarWallet.walletId, event.amount);

    action.subscribe(wallet => {
      this.dinarWallet = wallet;
    }, error => {
      alert('Erreur: ' + error.message);
    });
  }

  convertDinars() {
    if (this.convertAmount <= 0 || !this.equaWalletExists) return;

    this.apiService.convertDinarsToEqua(this.convertAmount).subscribe(wallet => {
      this.equaWallet = wallet;
      // Reload dinar wallet to reflect changes
      this.loadWallets();
    }, error => {
      alert('Erreur: ' + error.message);
    });
  }
}
