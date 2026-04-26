import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-market-explanation',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './market-explanation.component.html',
    styleUrl: './market-explanation.component.scss'
})
export class MarketExplanationComponent {
    steps = [
        {
            title: 'Approvisionnez votre portefeuille',
            description: 'Déposez des Dinars Tunisiens (TND) ou convertissez vos EQUA pour avoir des fonds disponibles.',
            icon: 'pi-wallet',
            color: '#2ecc71'
        },
        {
            title: 'Choisissez un actif',
            description: 'Parcourez l\'explorateur d\'actifs pour trouver des opportunités d\'investissement vérifiées.',
            icon: 'pi-search',
            color: '#627eea'
        },
        {
            title: 'Analysez les graphiques',
            description: 'Obtenez des informations en temps réel sur les tendances de prix et les volumes de transaction.',
            icon: 'pi-chart-line',
            color: '#9b59b6'
        },
        {
            title: 'Achetez ou Vendez',
            description: 'Exécutez vos ordres instantanément grâce à notre moteur de correspondance blockchain.',
            icon: 'pi-sync',
            color: '#e74c3c'
        }
    ];
}
