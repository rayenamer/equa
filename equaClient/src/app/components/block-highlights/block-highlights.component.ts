import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Block } from '../../models/block.model';

@Component({
    selector: 'app-block-highlights',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './block-highlights.component.html',
    styleUrls: ['./block-highlights.component.scss']
})
export class BlockHighlightsComponent {
    @Input() latestBlock?: Block = {
        blockId: 2456891,
        blockHash: '0x789ghi...jkl012',
        previousHash: '0xabc123...def456',
        timestamp: new Date(),
        blockSize: 1024567,
        previousBlockId: 2456890,
        createdAt: new Date(),
        updatedAt: new Date()
    };

    @Input() genesisBlock?: Block = {
        blockId: 0,
        blockHash: '0x000000...genesis',
        previousHash: '0x000000...000000',
        timestamp: new Date('2024-01-01T00:00:00'),
        blockSize: 512,
        previousBlockId: -1,
        createdAt: new Date('2024-01-01T00:00:00'),
        updatedAt: new Date('2024-01-01T00:00:00')
    };
}
