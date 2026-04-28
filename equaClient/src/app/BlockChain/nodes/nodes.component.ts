import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NodeStatsComponent } from '../../components/node-stats/node-stats.component';
import { NodesTableComponent } from '../../components/nodes-table/nodes-table.component';
import { NodeService } from '../services/node.service';
import { Node } from '../models/node.model';

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
export class NodesComponent implements OnInit {
  nodes = signal<Node[]>([]);
  totalNodes = signal<number>(0);
  onlineNodesCount = signal<number>(0);
  avgReputation = signal<number>(0);

  constructor(private nodeService: NodeService) { }

  ngOnInit(): void {
    this.loadNodes();
  }

  loadNodes(): void {
    this.nodeService.getAllNodes().subscribe({
      next: (nodes) => {
        // Convert reputation from (1-20, higher=worst) to percentage (0-100, higher=best)
        const processedNodes = nodes.map(node => ({
          ...node,
          reputationScore: this.convertToPercentage(node.reputationScore)
        }));
        this.nodes.set(processedNodes);
        this.calculateStats(processedNodes);
      },
      error: (err) => console.error('Error loading nodes:', err)
    });
  }

  private convertToPercentage(score: number): number {
    if (!score) return 0;
    // Scale 1 to 20: 1 is 100%, 20 is 0%
    // Formula: (1 - (score - 1) / 19) * 100
    const percentage = (1 - (score - 1) / 19) * 100;
    return Math.max(0, Math.min(100, Math.round(percentage)));
  }

  private calculateStats(nodes: Node[]): void {
    this.totalNodes.set(nodes.length);
    this.onlineNodesCount.set(nodes.filter(n => n.status === 'ONLINE').length);

    if (nodes.length > 0) {
      const sum = nodes.reduce((acc, n) => acc + (n.reputationScore || 0), 0);
      this.avgReputation.set(Math.round(sum / nodes.length));
    } else {
      this.avgReputation.set(0);
    }
  }
}
