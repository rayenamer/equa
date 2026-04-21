import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Transaction, TransactionStatus } from '../../BlockChain/models/transaction.model';

const MOCK_TRANSACTIONS: Transaction[] = [
    {
        transactionId: 1,
        fromWallet: '0x71C765...d897',
        toWallet: '0x123456...7890',
        amount: 1500.50,
        timestamp: new Date('2024-04-19T10:30:00'),
        status: TransactionStatus.COMPLETED,
        transactionHash: '0xabc123...def456',
        fee: 0.002
    },
    {
        transactionId: 2,
        fromWallet: '0x123456...7890',
        toWallet: '0x888888...9999',
        amount: 250.00,
        timestamp: new Date('2024-04-19T11:15:00'),
        status: TransactionStatus.PENDING,
        transactionHash: '0xdef456...abc123',
        fee: 0.0015
    },
    {
        transactionId: 3,
        fromWallet: '0x999999...1111',
        toWallet: '0x71C765...d897',
        amount: 5000.00,
        timestamp: new Date('2024-04-19T09:45:00'),
        status: TransactionStatus.FAILED,
        transactionHash: '0x789ghi...jkl012',
        fee: 0.005
    },
    {
        transactionId: 4,
        fromWallet: '0x222222...3333',
        toWallet: '0x444444...5555',
        amount: 12.75,
        timestamp: new Date('2024-04-18T22:10:00'),
        status: TransactionStatus.COMPLETED,
        transactionHash: '0xmno345...pqr678',
        fee: 0.0001
    },
    {
        transactionId: 5,
        fromWallet: '0x555555...6666',
        toWallet: '0x111111...2222',
        amount: 100.00,
        timestamp: new Date('2024-04-18T15:20:00'),
        status: TransactionStatus.PROCESSING,
        transactionHash: '0xstu789...vwx012',
        fee: 0.001
    }
];

import { TransactionFilters } from '../transaction-filters/transaction-filters.component';

import { TransactionStatsComponent } from '../transaction-stats/transaction-stats.component';
import { TransactionFiltersComponent } from '../transaction-filters/transaction-filters.component';

@Component({
    selector: 'app-transactions-table',
    standalone: true,
    imports: [
        CommonModule,
        TransactionStatsComponent,
        TransactionFiltersComponent
    ],
    templateUrl: './transactions-table.component.html',
    styleUrls: ['./transactions-table.component.scss'],
})
export class TransactionsTableComponent {
    private allTransactions = MOCK_TRANSACTIONS;
    private userWallet = '0x71C765...d897';

    activeFilter = signal<'all' | TransactionStatus | 'my'>('all');
    sortOrder = signal<'timestamp' | 'amount'>('timestamp');
    currentFilters = signal<TransactionFilters>({});
    currentPage = signal(1);
    pageSize = signal(10);

    statusTabs: { id: 'all' | TransactionStatus | 'my'; label: string }[] = [
        { id: 'all', label: 'TOUT' },
        { id: 'my', label: 'MES TRANSACTIONS' },
        { id: TransactionStatus.COMPLETED, label: 'COMPLÉTÉ' },
        { id: TransactionStatus.PENDING, label: 'EN ATTENTE' },
        { id: TransactionStatus.FAILED, label: 'ÉCHOUÉ' },
        { id: TransactionStatus.PROCESSING, label: 'TRAITEMENT' },
    ];

    filteredTransactions = computed(() => {
        let list = [...this.allTransactions];
        const statusFilter = this.activeFilter();
        const filters = this.currentFilters();
        const sortBy = this.sortOrder();

        // Status/My Transactions Filter
        if (statusFilter === 'my') {
            list = list.filter(t => t.fromWallet === this.userWallet || t.toWallet === this.userWallet);
        } else if (statusFilter !== 'all') {
            list = list.filter((t) => t.status === statusFilter);
        }

        // Search & Advanced Filters
        if (filters.walletSearch) {
            const term = filters.walletSearch.toLowerCase();
            list = list.filter(t =>
                t.fromWallet.toLowerCase().includes(term) ||
                t.toWallet.toLowerCase().includes(term)
            );
        }

        if (filters.amountThreshold !== undefined) {
            list = list.filter(t => t.amount >= (filters.amountThreshold || 0));
        }

        if (filters.startDate) {
            const start = new Date(filters.startDate).getTime();
            list = list.filter(t => new Date(t.timestamp).getTime() >= start);
        }

        if (filters.endDate) {
            const end = new Date(filters.endDate).getTime();
            list = list.filter(t => new Date(t.timestamp).getTime() <= end);
        }

        // Sorting
        if (sortBy === 'amount') {
            list.sort((a, b) => b.amount - a.amount);
        } else {
            list.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
        }

        return list;
    });

    totalItems = computed(() => this.filteredTransactions().length);

    paginatedTransactions = computed(() => {
        const list = this.filteredTransactions();
        const page = this.currentPage();
        const size = this.pageSize();
        const start = (page - 1) * size;
        return list.slice(start, start + size);
    });

    totalPages = computed(() =>
        Math.max(1, Math.ceil(this.totalItems() / this.pageSize()))
    );

    setFilter(id: 'all' | TransactionStatus | 'my'): void {
        this.activeFilter.set(id);
        this.currentPage.set(1);
    }

    setSort(order: 'timestamp' | 'amount'): void {
        this.sortOrder.set(order);
    }

    handleFiltersChange(filters: TransactionFilters) {
        this.currentFilters.set(filters);
        this.currentPage.set(1);
    }

    setPage(page: number): void {
        this.currentPage.set(Math.max(1, Math.min(page, this.totalPages())));
    }

    getStatusClass(status: TransactionStatus): string {
        switch (status) {
            case TransactionStatus.COMPLETED: return 'status-completed';
            case TransactionStatus.PENDING: return 'status-pending';
            case TransactionStatus.FAILED: return 'status-failed';
            case TransactionStatus.PROCESSING: return 'status-processing';
            default: return '';
        }
    }
}
