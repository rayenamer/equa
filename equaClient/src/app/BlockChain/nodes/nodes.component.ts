import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TokensGridComponent } from '../../components/tokens-grid/tokens-grid.component';
import { StatCardComponent } from '../../components/molecules/stat-card/stat-card.component';
import { TokenCard } from '../../models/token-card.model';

@Component({
  selector: 'app-nodes',
  standalone: true,
  imports: [
    CommonModule,
    TokensGridComponent,
    StatCardComponent
  ],
  templateUrl: './nodes.component.html',
  styleUrl: './nodes.component.scss'
})
export class NodesComponent {
  nodeStats = [
    { label: 'Total Nœuds', value: '156' },
    { label: 'En Ligne', value: '142' },
    { label: 'Top Réputation', value: '98.5' },
    { label: 'Stake Total (Dinar)', value: '1.2M' }
  ];

  nodes: TokenCard[] = [
    {
      title: 'Validator Oracle',
      subtitle: 'ONLINE',
      description: 'Réputation: 99.2% | Version: v2.1.0 | Localisation: Paris',
      image: '',
      borderColor: '#4ade80',
      gradient: 'linear-gradient(135deg, rgba(74, 222, 128, 0.2), #000)'
    },
    {
      title: 'Node Equa-1',
      subtitle: 'ONLINE',
      description: 'Réputation: 98.5% | Version: v2.1.0 | Localisation: Frankfurt',
      image: '',
      borderColor: '#4ade80',
      gradient: 'linear-gradient(135deg, rgba(74, 222, 128, 0.2), #000)'
    },
    {
      title: 'Titan Validator',
      subtitle: 'OFFLINE',
      description: 'Dernier bloc: #2,450,123 | Statut: Maintenance',
      image: '',
      borderColor: '#f87171',
      gradient: 'linear-gradient(135deg, rgba(248, 113, 113, 0.2), #000)'
    },
    {
      title: 'Dinar Forge',
      subtitle: 'ONLINE',
      description: 'Réputation: 97.8% | Version: v2.0.8 | Localisation: Singapore',
      image: '',
      borderColor: '#4ade80',
      gradient: 'linear-gradient(135deg, rgba(74, 222, 128, 0.2), #000)'
    }
  ];
}
