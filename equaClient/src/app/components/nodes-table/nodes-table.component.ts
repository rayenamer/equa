import { Component, computed, signal, Input, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Node } from '../../BlockChain/models/node.model';

@Component({
    selector: 'app-nodes-table',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './nodes-table.component.html',
    styleUrls: ['./nodes-table.component.scss']
})
export class NodesTableComponent {
    @Input() set nodes(value: Node[]) {
        this.allNodes.set(value);
    }

    private allNodes = signal<Node[]>([]);
    filter = signal<'all' | 'online' | 'top'>('all');

    filteredNodes = computed(() => {
        let list = [...this.allNodes()];
        if (this.filter() === 'online') {
            list = list.filter(n => n.status === 'ONLINE');
        } else if (this.filter() === 'top') {
            list = [...list].sort((a, b) => (b.reputationScore || 0) - (a.reputationScore || 0)).slice(0, 3);
        }
        return list;
    });

    setFilter(f: 'all' | 'online' | 'top') {
        this.filter.set(f);
    }

    getStatusClass(status: string) {
        return status?.toUpperCase() === 'ONLINE' ? 'status-online' : 'status-offline';
    }

    getReputationColor(score: number) {
        if (!score) return '#f87171';
        if (score >= 90) return '#4ade80';
        if (score >= 70) return '#facc15';
        return '#f87171';
    }
}
