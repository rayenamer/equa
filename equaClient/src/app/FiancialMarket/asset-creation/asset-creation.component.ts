import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { FinancialMarketService } from '../financial-market.service';
import { AssetRequestFinancial, AssetCategory, AssetResponseFinancial } from '../models/financial-market.models';

@Component({
    selector: 'app-asset-creation',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './asset-creation.component.html',
    styleUrl: './asset-creation.component.scss'
})
export class AssetCreationComponent {
    asset: AssetRequestFinancial = {
        name: '',
        ticker: '',
        category: 'CRYPTO',
        description: '',
        logoUrl: '',
        initialPriceEqua: 0,
        totalSupply: 0,
        volume24h: 0
    };

    categories: AssetCategory[] = ['CRYPTO', 'AGRICULTURE', 'TECHNOLOGY', 'REAL_ESTATE'];

    isLoading = false;
    createdAsset: AssetResponseFinancial | null = null;
    errorMessage: string | null = null;

    constructor(
        private financialService: FinancialMarketService,
        private router: Router
    ) { }

    onTickerInput(event: Event) {
        const input = event.target as HTMLInputElement;
        input.value = input.value.toUpperCase();
        this.asset.ticker = input.value;
    }

    onSubmit() {
        if (this.isLoading) return;
        this.isLoading = true;
        this.errorMessage = null;

        this.financialService.createAsset(this.asset).subscribe({
            next: (res) => {
                this.isLoading = false;
                this.createdAsset = res;
            },
            error: (err) => {
                this.isLoading = false;
                this.errorMessage = err.error?.message || 'Erreur lors de la création de l\'actif.';
                console.error('Error creating asset', err);
            }
        });
    }

    goToAssets() {
        this.router.navigate(['/financial-market/assets']);
    }
}
