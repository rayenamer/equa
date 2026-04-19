import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Node } from '../../models/node.model';

const MOCK_NODES: Node[] = [
    {
        nodeId: 101,
        nodeType: 'VALIDATOR',
        ipAddress: '192.168.1.45',
        status: 'ONLINE',
        publicKey: '0xabc...def1',
        reputationScore: 98.5,
        lastSeen: new Date(),
        location: 'Paris, FR',
        connectedNodesCount: 12,
        hasTransaction: true,
        createdAt: new Date('2024-01-01'),
        updatedAt: new Date()
    },
    {
        nodeId: 102,
        nodeType: 'FULL_NODE',
        ipAddress: '10.0.0.12',
        status: 'ONLINE',
        publicKey: '0xghi...jkl2',
        reputationScore: 85.0,
        lastSeen: new Date(),
        location: 'Tunis, TN',
        connectedNodesCount: 8,
        hasTransaction: false,
        createdAt: new Date('2024-02-15'),
        updatedAt: new Date()
    },
    {
        nodeId: 103,
        nodeType: 'VALIDATOR',
        ipAddress: '172.16.5.89',
        status: 'OFFLINE',
        publicKey: '0xmno...pqr3',
        reputationScore: 45.2,
        lastSeen: new Date(Date.now() - 3600000),
        location: 'Berlin, DE',
        connectedNodesCount: 0,
        hasTransaction: true,
        createdAt: new Date('2024-03-10'),
        updatedAt: new Date()
    },
    {
        nodeId: 104,
        nodeType: 'ARCHIVE',
        ipAddress: '45.12.3.90',
        status: 'ONLINE',
        publicKey: '0xstu...vwx4',
        reputationScore: 92.1,
        lastSeen: new Date(),
        location: 'New York, US',
        connectedNodesCount: 25,
        hasTransaction: true,
        createdAt: new Date('2024-01-20'),
        updatedAt: new Date()
    }
];

@Component({
    selector: 'app-nodes-table',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './nodes-table.component.html',
    styleUrls: ['./nodes-table.component.scss']
})
export class NodesTableComponent {
    private allNodes = MOCK_NODES;

    filter = signal<'all' | 'online' | 'top'>('all');

    filteredNodes = computed(() => {
        let list = [...this.allNodes];
        if (this.filter() === 'online') {
            list = list.filter(n => n.status === 'ONLINE');
        } else if (this.filter() === 'top') {
            list = list.sort((a, b) => b.reputationScore - a.reputationScore).slice(0, 3);
        }
        return list;
    });

    setFilter(f: 'all' | 'online' | 'top') {
        this.filter.set(f);
    }

    getStatusClass(status: string) {
        return status === 'ONLINE' ? 'status-online' : 'status-offline';
    }

    getReputationColor(score: number) {
        if (score >= 90) return '#4ade80';
        if (score >= 70) return '#facc15';
        return '#f87171';
    }
}
