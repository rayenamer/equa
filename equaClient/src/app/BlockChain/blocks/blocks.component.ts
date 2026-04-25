import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatCardComponent } from '../../components/molecules/stat-card/stat-card.component';
import { BlocksTableComponent } from '../../components/blocks-table/blocks-table.component';
import { BlockHighlightsComponent } from '../../components/block-highlights/block-highlights.component';
import { BlockExplorerToolsComponent } from '../../components/block-explorer-tools/block-explorer-tools.component';
import { BlockService } from '../services/block.service';
import { Block } from '../models/block.model';

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
export class BlocksComponent implements OnInit {
  blocks = signal<Block[]>([]);
  latestBlock = signal<Block | undefined>(undefined);
  genesisBlock = signal<Block | undefined>(undefined);

  blockStats = signal([
    { label: 'Nombre de Blocks', value: '...' },
    { label: 'Temps de Bloc Moy.', value: '...' },
    { label: 'Taille Moy. (TX)', value: '...' },
    { label: 'Dernière Actualisation', value: 'À l\'instant' }
  ]);

  constructor(private blockService: BlockService) { }

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.blockService.getAllBlocks().subscribe({
      next: (blocks) => {
        this.blocks.set(blocks);
      },
      error: (err) => console.error('Error loading blocks:', err)
    });

    this.blockService.getLatestBlock().subscribe({
      next: (block) => this.latestBlock.set(block),
      error: (err) => console.error('Error loading latest block:', err)
    });

    this.blockService.getGenesisBlock().subscribe({
      next: (block) => this.genesisBlock.set(block),
      error: (err) => console.error('Error loading genesis block:', err)
    });

    this.blockService.getBlockchainStats().subscribe({
      next: (stats) => {
        this.blockStats.set([
          { label: 'Nombre de Blocks', value: `#${stats.totalBlocks || 0}` },
          { label: 'Taille Moy. (TX)', value: `${stats.averageBlockSize || 0} tx` },
          { label: 'Dernière Actualisation', value: 'À l\'instant' }
        ]);
      },
      error: (err) => console.error('Error loading stats:', err)
    });
  }
}
