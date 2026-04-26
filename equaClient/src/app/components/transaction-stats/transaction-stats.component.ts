import { Component, Input, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TransactionService } from '../../BlockChain/services/transaction.service';
import { ApiService } from '../../services/api.service';
import { forkJoin, switchMap } from 'rxjs';

@Component({
    selector: 'app-transaction-stats',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './transaction-stats.component.html',
    styleUrls: ['./transaction-stats.component.scss']
})
export class TransactionStatsComponent implements OnInit {
    userWallet = signal<string>('');

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
}
