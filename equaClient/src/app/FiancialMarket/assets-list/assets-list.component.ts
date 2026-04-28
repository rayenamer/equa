import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FinancialMarketService } from '../financial-market.service';
import { AssetResponseFinancial } from '../models/financial-market.models';

@Component({
    selector: 'app-assets-list',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule],
    templateUrl: './assets-list.component.html',
    styleUrl: './assets-list.component.scss'
})
export class AssetsListComponent implements OnInit {
    searchQuery: string = '';
    selectedCategory: string = 'All';

    categories = ['All', 'Agriculture', 'Technology', 'Real Estate', 'Crypto'];

    assets: AssetResponseFinancial[] = [];

    constructor(private financialService: FinancialMarketService) { }

    ngOnInit(): void {
        this.loadAssets();
    }

    loadAssets() {
        this.financialService.getAllAssets().subscribe({
            next: (data) => {
                this.assets = data;
            },
            error: (err) => {
                console.error('Error fetching assets', err);
            }
        });
    }

    filteredAssets() {
        return this.assets.filter(asset => {
            const matchesSearch = asset.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
                asset.ticker.toLowerCase().includes(this.searchQuery.toLowerCase());
            const matchesCategory = this.selectedCategory === 'All' || asset.category === this.selectedCategory;
            return matchesSearch && matchesCategory;
        });
    }

    marketCap(asset: AssetResponseFinancial): number {
        return asset.currentPriceEqua * asset.totalSupply;
    }
}
