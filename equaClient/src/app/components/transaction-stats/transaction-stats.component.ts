import { Component, Input, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TransactionService } from '../../BlockChain/services/transaction.service';
import { ApiService } from '../../services/api.service';
import { forkJoin, switchMap } from 'rxjs';

@Component({
    selector: 'app-transaction-stats',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './transaction-stats.component.html',
    styleUrls: ['./transaction-stats.component.scss']
})
export class TransactionStatsComponent implements OnInit {
    userWallet = signal<string>('');
    generatedLink = signal<string>('');

    // Modal state
    isModalOpen = signal(false);
    requestAmount = signal<number | null>(null);
    modalError = signal<string>('');
    toastMessage = signal<string>('');

    totalFees = signal(0);
    avgAmount = signal(0);
    totalSent = signal(0);
    totalReceived = signal(0);

    constructor(
        private transactionService: TransactionService,
        private apiService: ApiService
    ) { }

    ngOnInit(): void {
        this.loadStats();
    }

    loadStats(): void {
        this.apiService.getMyWallet().pipe(
            switchMap(wallet => {
                const walletId = wallet.id.toString();
                this.userWallet.set(walletId);
                return forkJoin({
                    average: this.transactionService.getAverageTransactionAmount(),
                    sent: this.transactionService.getTotalSentByWallet(walletId),
                    received: this.transactionService.getTotalReceivedByWallet(walletId)
                });
            })
        ).subscribe({
            next: (data) => {
                this.avgAmount.set(data.average || 0);
                this.totalSent.set(data.sent || 0);
                this.totalReceived.set(data.received || 0);
                this.totalFees.set(0); // Keep fee 0 as requested
            },
            error: (err) => console.error('Error loading transaction stats:', err)
        });
    }

    openRequestModal(): void {
        const myWalletId = this.userWallet();
        if (!myWalletId) {
            this.showToast('Portefeuille non chargé.');
            return;
        }
        this.requestAmount.set(null);
        this.modalError.set('');
        this.isModalOpen.set(true);
    }

    closeModal(): void {
        this.isModalOpen.set(false);
    }

    confirmRequest(): void {
        const amount = this.requestAmount();
        if (amount == null || amount <= 0) {
            this.modalError.set('Veuillez entrer un montant valide supérieur à 0.');
            return;
        }

        const myWalletId = this.userWallet();
        if (!myWalletId) {
            this.modalError.set('ID du portefeuille manquant.');
            return;
        }

        const link = `${window.location.origin}/blockchain/transactions/create?toWallet=${myWalletId}&amount=${amount}`;
        this.generatedLink.set(link);
        this.closeModal();
    }

    copyLink(): void {
        const link = this.generatedLink();
        if (link) {
            navigator.clipboard.writeText(link).then(() => {
                this.showToast('Lien copié dans le presse-papiers !');
            }).catch(err => {
                console.error('Failed to copy', err);
                this.showToast('Échec de la copie du lien.');
            });
        }
    }

    showToast(message: string): void {
        this.toastMessage.set(message);
        setTimeout(() => this.toastMessage.set(''), 3000);
    }
}
