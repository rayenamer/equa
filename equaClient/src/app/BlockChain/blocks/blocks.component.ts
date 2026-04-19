import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatCardComponent } from '../../components/molecules/stat-card/stat-card.component';
import { BlocksTableComponent } from '../../components/blocks-table/blocks-table.component';
import { BlockHighlightsComponent } from '../../components/block-highlights/block-highlights.component';
import { BlockExplorerToolsComponent } from '../../components/block-explorer-tools/block-explorer-tools.component';

@Component({
  selector: 'app-blocks',
  standalone: true,
  imports: [
    CommonModule,
    BlocksTableComponent,
    StatCardComponent,
    BlockHighlightsComponent,
    BlockExplorerToolsComponent
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
