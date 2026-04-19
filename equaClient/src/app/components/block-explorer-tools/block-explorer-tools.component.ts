import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-block-explorer-tools',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './block-explorer-tools.component.html',
    styleUrls: ['./block-explorer-tools.component.scss']
})
export class BlockExplorerToolsComponent {
    searchHash = '';
    searchResult: 'found' | 'not-found' | 'idle' = 'idle';
    isChecking = false;

    checkBlock() {
        if (!this.searchHash) return;

        this.isChecking = true;
        this.searchResult = 'idle';

        // Simulate API call to /exists/{blockHash}
        setTimeout(() => {
            this.isChecking = false;
            // Mock logic: hashes ending in 'e' exist
            this.searchResult = this.searchHash.toLowerCase().endsWith('e') ? 'found' : 'not-found';
        }, 1000);
    }
}
