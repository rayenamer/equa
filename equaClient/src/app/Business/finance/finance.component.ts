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
            icon: '💧',
            description: 'Mesure la capacité à honorer les dettes à court terme.'
        },
        {
            nom: 'Ratio de liquidité réduite',
            formule: '(Actif courant – Stocks) / Passif courant',
            valeur: 1.87,
            seuil: '> 1',
            statut: 'bon',
            icon: '🔵',
            description: 'Version plus stricte excluant les stocks.'
        },
        {
            nom: 'Ratio de liquidité immédiate',
            formule: 'Disponibilités / Passif courant',
            valeur: 0.48,
            seuil: '> 0.2',
            statut: 'moyen',
            icon: '💰',
            description: 'Trésorerie instantanée face aux dettes immédiates.'
        }
    ];

    ratiosSolvabilite = [
        {
            nom: 'Ratio d\'endettement',
            formule: 'Dettes totales / Capitaux propres',
            valeur: 0.65,
            seuil: '< 1',
            statut: 'bon',
            icon: '⚖️',
            description: 'Structure financière et dépendance aux créanciers.'
        },
        {
            nom: 'Ratio d\'autonomie financière',
            formule: 'Capitaux propres / Total passif',
            valeur: 0.61,
            seuil: '> 0.5',
            statut: 'bon',
            icon: '🏛️',
            description: 'Part des actifs financés par fonds propres.'
        },
        {
            nom: 'Gearing',
            formule: 'Dettes financières nettes / Capitaux propres',
            valeur: 0.32,
            seuil: '< 1',
            statut: 'bon',
            icon: '⚙️',
            description: 'Levier financier net de trésorerie.'
        }
    ];

    ratiosProfitabilite = [
        {
            nom: 'Marge bénéficiaire nette',
            formule: 'Résultat net / CA',
            valeur: '12.4%',
            seuil: '',
            statut: 'bon',
            icon: '📈',
            description: 'Part du chiffre d\'affaires convertie en profit.'
        },
        {
            nom: 'ROE (Return on Equity)',
            formule: 'Résultat net / Capitaux propres',
            valeur: '18.7%',
            seuil: '',
            statut: 'bon',
            icon: '🎯',
            description: 'Rentabilité des capitaux propres investis.'
        },
        {
            nom: 'ROA (Return on Assets)',
            formule: 'Résultat net / Total actif',
            valeur: '8.2%',
            seuil: '',
            statut: 'bon',
            icon: '🏗️',
            description: 'Efficacité des actifs à générer du profit.'
        },
        {
            nom: 'EBITDA Margin',
            formule: 'EBITDA / CA',
            valeur: '24.1%',
            seuil: '',
            statut: 'bon',
            icon: '💼',
            description: 'Rentabilité avant intérêts, taxes et amortissements.'
        }
    ];

    setTab(tab: 'ratios' | 'etats') {
        this.activeTab = tab;
    }
}
