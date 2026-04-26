import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-asset-creation',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './asset-creation.component.html',
    styleUrl: './asset-creation.component.scss'
})
export class AssetCreationComponent {
    asset = {
        name: '',
        symbol: '',
        initialPrice: 0,
        totalSupply: 0,
        category: 'Technology',
        description: '',
        logo: null
    };

    categories = ['Agriculture', 'Technology', 'Real Estate', 'Crypto', 'Services'];

    onSubmit() {
        console.log('Asset creation request submitted:', this.asset);
        alert('Votre demande de création d\'actif a été soumise avec succès et est en attente de vérification.');
    }
}
