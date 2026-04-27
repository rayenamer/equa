import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { FinancialMarketService } from '../financial-market.service';
import { AssetRequestFinancial } from '../models/financial-market.models';

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
        initialPrice: 0,
        totalSupply: 0,
        category: 'Technology',
        description: ''
    };

    categories = ['Agriculture', 'Technology', 'Real Estate', 'Crypto', 'Services'];

    constructor(
        private financialService: FinancialMarketService,
        private router: Router
    ) { }

    onSubmit() {
        this.financialService.createAsset(this.asset).subscribe({
            next: (res) => {
                alert('Votre actif a été créé avec succès.');
                this.router.navigate(['/financial-market/assets']);
            },
            error: (err) => {
                console.error('Error creating asset', err);
                alert('Erreur lors de la création de l\'actif.');
            }
        });
    }
}
