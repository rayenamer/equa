import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';

interface Mouvement {
    id: number;
    date: string;
    libelle: string;
    type: 'sortant' | 'entrant';
    compte: string;
    montant: number;
    categorie: string;
    statut: 'valide' | 'en_attente' | 'annule';
}

@Component({
    selector: 'app-finance',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './finance.component.html',
    styleUrl: './finance.component.scss'
})
export class FinanceComponent implements OnInit {
    activeTab: 'ratios' | 'etats' = 'ratios';

    ratiosLiquidite: any[] = [];
    ratiosSolvabilite: any[] = [];
    ratiosProfitabilite: any[] = [];

    // Local state for financial states manually populated
    caTotal: number = 0;
    achatsTotal: number = 0;
    chargesPersonnel: number = 0;
    autresCharges: number = 0;
    subventions: number = 0;

    constructor(private apiService: ApiService) { }

    ngOnInit() {
        this.apiService.getBusinesses().subscribe(businesses => {
            if (businesses && businesses.length > 0) {
                // Fetch mouvements directly to calculate ratios client-side
                this.apiService.getMouvements(businesses[0].id).subscribe((mouvements: Mouvement[]) => {
                    this.calculateRatiosLocally(mouvements);
                });
            }
        });
    }

    calculateRatiosLocally(mouvements: Mouvement[]) {
        const validMouvements = mouvements.filter(m => m.statut !== 'annule');

        let totalEntrant = 0;
        let totalSortant = 0;
        let chiffreAffaires = 0;
        let chargesOperationnelles = 0;

        // Populate statements data
        this.caTotal = 0;
        this.achatsTotal = 0;
        this.chargesPersonnel = 0;
        this.autresCharges = 0;
        this.subventions = 0;

        validMouvements.forEach(m => {
            if (m.type === 'entrant') totalEntrant += m.montant;
            if (m.type === 'sortant') totalSortant += m.montant;

            if (m.categorie === 'Chiffre d\'affaires') {
                chiffreAffaires += m.montant;
                this.caTotal += m.montant;
            } else if (m.categorie.toLowerCase().includes('subvention')) {
                this.subventions += m.montant;
            } else if (m.categorie.startsWith('Charges')) {
                chargesOperationnelles += m.montant;
                if (m.categorie === 'Charges de personnel') {
                    this.chargesPersonnel += m.montant;
                } else if (m.categorie.toLowerCase().includes('achats')) {
                    this.achatsTotal += m.montant;
                } else {
                    this.autresCharges += m.montant;
                }
            } else {
                // Fallback classifications based on type just for reporting
                if (m.type === 'entrant' && m.categorie !== 'Chiffre d\'affaires' && !m.categorie.toLowerCase().includes('subvention')) {
                    // Let's add unexpected income to subventions or leave as CA
                    this.subventions += m.montant;
                } else if (m.type === 'sortant' && !m.categorie.startsWith('Charges')) {
                    this.autresCharges += m.montant;
                }
            }
        });

        // Basic calculation of Ratios
        const solde = totalEntrant - totalSortant;

        // Liquidité
        const liquiditeRatio = totalSortant > 0 ? (totalEntrant / totalSortant).toFixed(2) : '-';
        this.ratiosLiquidite = [
            {
                nom: 'Liquidité Générale',
                valeur: liquiditeRatio,
                formule: 'Recettes Totales / Dépenses Totales',
                seuil: '> 1.2',
                statut: totalSortant === 0 ? 'bon' : (totalEntrant / totalSortant > 1.2 ? 'bon' : 'moyen'),
                description: 'Capacité à couvrir les dépenses avec les entrées.',
                icon: '💧'
            }
        ];

        // Solvabilité
        const solvabiliteRatio = chargesOperationnelles > 0 ? (solde / chargesOperationnelles).toFixed(2) : '-';
        this.ratiosSolvabilite = [
            {
                nom: 'Couverture des Charges',
                valeur: solvabiliteRatio,
                formule: 'Trésorerie Nette / Charges Opérationnelles',
                seuil: '> 0.5',
                statut: chargesOperationnelles === 0 ? 'bon' : (solde / chargesOperationnelles > 0.5 ? 'bon' : 'risque'),
                description: 'Solde de trésorerie disponible pour couvrir les charges régulières.',
                icon: '🛡️'
            }
        ];

        // Rentabilité (Profitability)
        const resultatNet = totalEntrant - totalSortant; // Simplification
        const profitabiliteRatio = chiffreAffaires > 0 ? ((resultatNet / chiffreAffaires) * 100).toFixed(1) + '%' : '-';
        this.ratiosProfitabilite = [
            {
                nom: 'Marge Nette',
                valeur: profitabiliteRatio,
                formule: '(Recettes - Dépenses) / Chiffre d\'Affaires',
                statut: chiffreAffaires === 0 ? 'moyen' : (resultatNet / chiffreAffaires > 0.1 ? 'bon' : 'moyen'),
                description: 'Pourcentage de bénéfice net par rapport au chiffre d\'affaires.',
                icon: '📈'
            }
        ];
    }

    setTab(tab: 'ratios' | 'etats') {
        this.activeTab = tab;
    }
}
