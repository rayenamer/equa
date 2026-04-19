import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TransactionsTableComponent } from '../../../components/transactions-table/transactions-table.component';

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
export class ListComponent {
}
