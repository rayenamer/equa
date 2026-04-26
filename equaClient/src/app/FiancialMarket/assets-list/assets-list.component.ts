import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-assets-list',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule],
    templateUrl: './assets-list.component.html',
    styleUrl: './assets-list.component.scss'
})
export class AssetsListComponent {
    searchQuery: string = '';
    selectedCategory: string = 'All';

    categories = ['All', 'Agriculture', 'Technology', 'Real Estate', 'Crypto'];

    assets = [
        { id: 1, name: 'EQUA Token', symbol: 'EQUA', price: 1.20, change: +2.5, cap: '1.2M', volume: '450K', category: 'Crypto', color: '#627eea' },
        { id: 2, name: 'Olive Capital', symbol: 'OLV', price: 45.80, change: -1.2, cap: '25M', volume: '120K', category: 'Agriculture', color: '#27ae60' },
        { id: 3, name: 'Sousse Tech', symbol: 'STK', price: 12.30, change: +15.8, cap: '8M', volume: '300K', category: 'Technology', color: '#f1c40f' },
        { id: 4, name: 'Sahara Solaris', symbol: 'SOL', price: 0.85, change: +0.4, cap: '45M', volume: '80K', category: 'Technology', color: '#e67e22' },
        { id: 5, name: 'Carthage Estates', symbol: 'CRT', price: 1250.00, change: -0.1, cap: '150M', volume: '5K', category: 'Real Estate', color: '#95a5a6' },
    ];

    filteredAssets() {
        return this.assets.filter(asset => {
            const matchesSearch = asset.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
                asset.symbol.toLowerCase().includes(this.searchQuery.toLowerCase());
            const matchesCategory = this.selectedCategory === 'All' || asset.category === this.selectedCategory;
            return matchesSearch && matchesCategory;
        });
    }
}
