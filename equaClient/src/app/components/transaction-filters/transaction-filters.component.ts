import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface TransactionFilters {
    walletSearch?: string;
    amountThreshold?: number;
    startDate?: string;
    endDate?: string;
    nodeId?: number;
}

@Component({
    selector: 'app-transaction-filters',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './transaction-filters.component.html',
    styleUrls: ['./transaction-filters.component.scss']
})
export class TransactionFiltersComponent {
    @Output() filtersChanged = new EventEmitter<TransactionFilters>();

    filters: TransactionFilters = {
        walletSearch: '',
        amountThreshold: undefined,
        startDate: '',
        endDate: '',
        nodeId: undefined
    };

    applyFilters() {
        this.filtersChanged.emit({ ...this.filters });
    }

    clearFilters() {
        this.filters = {
            walletSearch: '',
            amountThreshold: undefined,
            startDate: '',
            endDate: '',
            nodeId: undefined
        };
        this.applyFilters();
    }
}
