import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TransactionsTableComponent } from '../../../components/transactions-table/transactions-table.component';
import { TransactionService } from '../../services/transaction.service';
import { Transaction } from '../../models/transaction.model';

@Component({
  selector: 'app-list',
  standalone: true,
  imports: [
    CommonModule,
    TransactionsTableComponent
  ],
  templateUrl: './list.component.html',
  styleUrl: './list.component.scss'
})
export class ListComponent implements OnInit {
  transactions = signal<Transaction[]>([]);

  constructor(private transactionService: TransactionService) { }

  ngOnInit(): void {
    this.transactionService.getAllTransactions().subscribe({
      next: (data) => this.transactions.set(data),
      error: (err) => console.error('Error fetching transactions:', err)
    });
  }
}
