import { Component, Input, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Transaction } from '../../BlockChain/models/transaction.model';

@Component({
    selector: 'app-transaction-stats',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './transaction-stats.component.html',
    styleUrls: ['./transaction-stats.component.scss']
})
export class TransactionStatsComponent {
    @Input() transactions: Transaction[] = [];
    @Input() userWallet: string = '0x71C765...d897';

    totalFees = computed(() =>
        this.transactions.reduce((sum, tx) => sum + tx.fee, 0)
    );

    avgAmount = computed(() =>
        this.transactions.length > 0
            ? this.transactions.reduce((sum, tx) => sum + tx.amount, 0) / this.transactions.length
            : 0
    );

    totalSent = computed(() =>
        this.transactions
            .filter(tx => tx.fromWallet === this.userWallet)
            .reduce((sum, tx) => sum + tx.amount, 0)
    );

    totalReceived = computed(() =>
        this.transactions
            .filter(tx => tx.toWallet === this.userWallet)
            .reduce((sum, tx) => sum + tx.amount, 0)
    );

    countByStatus(status: string) {
        return this.transactions.filter(tx => tx.status === status).length;
    }
}
