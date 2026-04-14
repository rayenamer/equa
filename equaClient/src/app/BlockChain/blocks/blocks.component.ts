import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MarketsTableComponent } from '../../components/markets-table/markets-table.component';
import { StatCardComponent } from '../../components/molecules/stat-card/stat-card.component';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';

@Component({
  selector: 'app-blocks',
  standalone: true,
  imports: [
    CommonModule,
    MarketsTableComponent,
    StatCardComponent,
    UiInputComponent
  ],
  templateUrl: './blocks.component.html',
  styleUrl: './blocks.component.scss'
})
export class BlocksComponent {
  blockStats = [
    { label: 'Hauteur Bloc', value: '#2,456,891' },
    { label: 'Temps de Bloc Moy.', value: '1.2s' },
    { label: 'Taille Moy. Bloc', value: '45 KB' },
    { label: 'Dernière Actualisation', value: 'À l\'instant' }
  ];
}
