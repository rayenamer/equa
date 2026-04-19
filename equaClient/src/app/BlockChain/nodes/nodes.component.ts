import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NodeStatsComponent } from '../../components/node-stats/node-stats.component';
import { NodesTableComponent } from '../../components/nodes-table/nodes-table.component';

@Component({
  selector: 'app-nodes',
  standalone: true,
  imports: [
    CommonModule,
    NodeStatsComponent,
    NodesTableComponent
  ],
  templateUrl: './nodes.component.html',
  styleUrl: './nodes.component.scss'
})
export class NodesComponent {
}
