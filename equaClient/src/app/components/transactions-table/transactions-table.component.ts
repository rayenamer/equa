import { Component, computed, signal, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Transaction, TransactionStatus } from '../../BlockChain/models/transaction.model';

import { TransactionStatsComponent } from '../transaction-stats/transaction-stats.component';
import { TransactionFiltersComponent, TransactionFilters } from '../transaction-filters/transaction-filters.component';

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
    private transactionsSignal = signal<Transaction[]>([]);

    @Input() set transactions(value: Transaction[]) {
        this.transactionsSignal.set(value || []);
    }

    get transactions(): Transaction[] {
        return this.transactionsSignal();
    }

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
        let list = [...this.transactions];
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
