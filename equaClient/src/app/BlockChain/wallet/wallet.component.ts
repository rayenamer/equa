import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DinarBalanceCardComponent } from '../../components/dinar-balance-card/dinar-balance-card.component';
import { DinarActionsComponent } from '../../components/dinar-actions/dinar-actions.component';
import { DinarInventoryComponent } from '../../components/dinar-inventory/dinar-inventory.component';
import { DinarWallet, Dinar } from '../../models/dinar-wallet.model';

@Component({
  selector: 'app-wallet',
  standalone: true,
  imports: [
    CommonModule,
    DinarBalanceCardComponent,
    DinarActionsComponent,
    DinarInventoryComponent
  ],
  templateUrl: './wallet.component.html',
  styleUrl: './wallet.component.scss'
})
export class WalletComponent {
  myWallet: DinarWallet | null = {
    walletId: 'DW-452-901-QX',
    balance: 45280.00,
    ownerId: 1,
    dinars: [
      { serialNumber: 'TND-001-A92', value: 10000, issueDate: new Date('2024-01-10'), ownerWalletId: 'DW-452-901-QX' },
      { serialNumber: 'TND-001-B45', value: 20000, issueDate: new Date('2024-02-15'), ownerWalletId: 'DW-452-901-QX' },
      { serialNumber: 'TND-002-C12', value: 15280, issueDate: new Date('2024-03-05'), ownerWalletId: 'DW-452-901-QX' }
    ],
    createdAt: new Date('2023-12-20'),
    updatedAt: new Date()
  };

  handleWalletAction(event: { type: 'deposit' | 'withdraw', amount: number }) {
    if (!this.myWallet) return;

    if (event.type === 'deposit') {
      this.myWallet.balance += event.amount;
      const newDinar: Dinar = {
        serialNumber: `TND-NEW-${Math.floor(Math.random() * 1000)}`,
        value: event.amount,
        issueDate: new Date(),
        ownerWalletId: this.myWallet.walletId
      };
      this.myWallet.dinars = [newDinar, ...this.myWallet.dinars];
    } else {
      if (this.myWallet.balance >= event.amount) {
        this.myWallet.balance -= event.amount;
        // In a real app, we'd remove specific Dinars, here we just update balance for demo
      } else {
        alert('Solde insuffisant !');
      }
    }
  }

  createWallet() {
    this.myWallet = {
      walletId: `DW-${Math.floor(Math.random() * 1000)}-${Math.floor(Math.random() * 1000)}`,
      balance: 0,
      ownerId: 1,
      dinars: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };
  }
}
