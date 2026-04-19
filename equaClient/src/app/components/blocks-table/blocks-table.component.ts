import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Block } from '../../models/block.model';

const MOCK_BLOCKS: Block[] = [
    {
        blockId: 2456891,
        previousHash: '0xabc123...def456',
        blockHash: '0x789ghi...jkl012',
        timestamp: new Date('2024-04-19T10:30:00'),
        blockSize: 1024567,
        previousBlockId: 2456890,
        createdAt: new Date('2024-04-19T10:30:00'),
        updatedAt: new Date('2024-04-19T10:30:05')
    },
    {
        blockId: 2456890,
        previousHash: '0xmno345...pqr678',
        blockHash: '0xabc123...def456',
        timestamp: new Date('2024-04-19T10:25:00'),
        blockSize: 985432,
        previousBlockId: 2456889,
        createdAt: new Date('2024-04-19T10:25:00'),
        updatedAt: new Date('2024-04-19T10:25:04')
    },
    {
        blockId: 2456889,
        previousHash: '0xstu789...vwx012',
        blockHash: '0xmno345...pqr678',
        timestamp: new Date('2024-04-19T10:20:00'),
        blockSize: 1120456,
        previousBlockId: 2456888,
        createdAt: new Date('2024-04-19T10:20:00'),
        updatedAt: new Date('2024-04-19T10:20:06')
    },
    {
        blockId: 2456888,
        previousHash: '0x123456...7890ab',
        blockHash: '0xstu789...vwx012',
        timestamp: new Date('2024-04-19T10:15:00'),
        blockSize: 856721,
        previousBlockId: 2456887,
        createdAt: new Date('2024-04-19T10:15:00'),
        updatedAt: new Date('2024-04-19T10:15:03')
    },
    {
        blockId: 2456887,
        previousHash: '0x567890...cdef01',
        blockHash: '0x123456...7890ab',
        timestamp: new Date('2024-04-19T10:10:00'),
        blockSize: 1045234,
        previousBlockId: 2456886,
        createdAt: new Date('2024-04-19T10:10:00'),
        updatedAt: new Date('2024-04-19T10:10:05')
    }
];

@Component({
    selector: 'app-blocks-table',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './blocks-table.component.html',
    styleUrls: ['./blocks-table.component.scss'],
})
export class BlocksTableComponent {
    private allBlocks = MOCK_BLOCKS;

    currentPage = signal(1);
    pageSize = signal(10);

    paginatedBlocks = computed(() => {
        const list = this.allBlocks;
        const page = this.currentPage();
        const size = this.pageSize();
        const start = (page - 1) * size;
        return list.slice(start, start + size);
    });

    totalItems = computed(() => this.allBlocks.length);
    totalPages = computed(() => Math.max(1, Math.ceil(this.totalItems() / this.pageSize())));

    setPage(page: number): void {
        this.currentPage.set(Math.max(1, Math.min(page, this.totalPages())));
    }

    formatBytes(bytes: number): string {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }
}
