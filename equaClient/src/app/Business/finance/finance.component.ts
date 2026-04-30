import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-finance',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './finance.component.html',
    styleUrl: './finance.component.scss'
})
export class FinanceComponent {
    activeTab: 'ratios' | 'etats' = 'ratios';

    // TODO: replace all below with real API data from business finance endpoints
    // These are placeholder/demo values for template illustration

    ratiosLiquidite = [
        {
            nom: 'Ratio de liquidité générale',
            formule: 'Actif courant / Passif courant',
            valeur: 2.34,
            seuil: '> 1',
            statut: 'bon',
            icon: '',
            description: 'Mesure la capacité à honorer les dettes à court terme.'
        }
    ];

    ratiosSolvabilite = [
        {
            nom: 'Ratio d\'endettement',
            formule: 'Dettes totales / Capitaux propres',
            valeur: 0.65,
            seuil: '< 1',
            statut: 'bon',
            icon: '',
            description: 'Structure financière et dépendance aux créanciers.'
        }
    ];

    ratiosProfitabilite = [
        {
            nom: 'Marge bénéficiaire nette',
            formule: 'Résultat net / CA',
            valeur: '12.4%',
            seuil: '',
            statut: 'bon',
            icon: '',
            description: 'Part du chiffre d\'affaires convertie en profit.'
        },
        {
            nom: 'ROE (Return on Equity)',
            formule: 'Résultat net / Capitaux propres',
            valeur: '18.7%',
            seuil: '',
            statut: 'bon',
            icon: '',
            description: 'Rentabilité des capitaux propres investis.'
        }
    ];

    setTab(tab: 'ratios' | 'etats') {
        this.activeTab = tab;
    }
}
