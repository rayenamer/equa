import { Component, computed, signal, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Block } from '../../BlockChain/models/block.model';

@Component({
    selector: 'app-blocks-table',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './blocks-table.component.html',
    styleUrls: ['./blocks-table.component.scss'],
})
export class BlocksTableComponent {
    @Input() set blocks(value: Block[]) {
        this.allBlocks.set(value);
    }

    private allBlocks = signal<Block[]>([]);

    currentPage = signal(1);
    pageSize = signal(10);

    paginatedBlocks = computed(() => {
        const list = this.allBlocks();
        const page = this.currentPage();
        const size = this.pageSize();
        const start = (page - 1) * size;
        return list.slice(start, start + size);
    });

    totalItems = computed(() => this.allBlocks().length);
    totalPages = computed(() => Math.max(1, Math.ceil(this.totalItems() / this.pageSize())));
    setPage(page: number): void {
        this.currentPage.set(Math.max(1, Math.min(page, this.totalPages())));
    }
}
