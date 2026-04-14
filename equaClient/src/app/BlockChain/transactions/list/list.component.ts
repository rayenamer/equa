import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MarketsTableComponent } from '../../../components/markets-table/markets-table.component';
import { UiInputComponent } from '../../../components/atoms/ui-input/ui-input.component';
import { UiButtonComponent } from '../../../components/atoms/ui-button/ui-button.component';

@Component({
  selector: 'app-list',
  standalone: true,
  imports: [
    CommonModule,
    MarketsTableComponent,
    UiInputComponent,
    UiButtonComponent
  ],
  templateUrl: './list.component.html',
  styleUrl: './list.component.scss'
})
export class ListComponent {
  // Static filters for UI simulation
  statusFilters = ['Tous', 'Confirmé', 'En attente', 'Échoué'];
}
