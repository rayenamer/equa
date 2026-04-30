import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface Mouvement {
    id: number;
    date: string;
    libelle: string;
    type: 'debit' | 'credit';
    compte: string;
    montant: number;
    categorie: string;
    statut: 'valide' | 'en_attente' | 'annule';
}

@Component({
    selector: 'app-mouvements',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './mouvements.component.html',
    styleUrl: './mouvements.component.scss'
})
export class MouvementsComponent {
    activeTab: 'comptabilite' | 'nouveau' = 'comptabilite';
    filterType: 'all' | 'debit' | 'credit' = 'all';
    searchTerm = '';

    // ─── Sample data for analytical accounting display ───────────────────────
    mouvements: Mouvement[] = [
        { id: 1, date: '2026-04-28', libelle: 'Vente produit A', type: 'credit', compte: '701 – Ventes marchandises', montant: 125000, categorie: 'Chiffre d\'affaires', statut: 'valide' },
        { id: 2, date: '2026-04-27', libelle: 'Achat matières premières', type: 'debit', compte: '601 – Achats matières', montant: 48000, categorie: 'Charges opérationnelles', statut: 'valide' },
        { id: 3, date: '2026-04-26', libelle: 'Salaires Avril', type: 'debit', compte: '641 – Charges de personnel', montant: 72000, categorie: 'Charges de personnel', statut: 'valide' },
        { id: 4, date: '2026-04-25', libelle: 'Loyer bureau', type: 'debit', compte: '613 – Locations', montant: 18000, categorie: 'Charges générales', statut: 'valide' },
        { id: 5, date: '2026-04-24', libelle: 'Remboursement client', type: 'debit', compte: '411 – Clients', montant: 5000, categorie: 'Clients', statut: 'en_attente' },
        { id: 6, date: '2026-04-23', libelle: 'Service consulting', type: 'credit', compte: '706 – Prestations services', montant: 37500, categorie: 'Chiffre d\'affaires', statut: 'valide' },
    ];

    // ─── New movement form ────────────────────────────────────────────────────
    newMouvement = {
        date: new Date().toISOString().slice(0, 10),
        libelle: '',
        type: 'credit' as 'credit' | 'debit',
        compte: '',
        montant: null as number | null,
        categorie: '',
        statut: 'en_attente' as 'valide' | 'en_attente' | 'annule',
        description: ''
    };

    comptes = [
        '411 – Clients',
        '401 – Fournisseurs',
        '512 – Banque',
        '601 – Achats matières',
        '606 – Fournitures bureau',
        '613 – Locations',
        '616 – Primes assurances',
        '641 – Charges de personnel',
        '695 – Impôts sur bénéfices',
        '701 – Ventes marchandises',
        '706 – Prestations services',
        '707 – Ventes produits',
        '741 – Subventions exploitation',
    ];

    categories = [
        'Chiffre d\'affaires',
        'Charges opérationnelles',
        'Charges de personnel',
        'Charges générales',
        'Clients',
        'Fournisseurs',
        'Investissements',
        'Financier',
        'Exceptionnel',
    ];

    formSubmitted = false;
    showSuccess = false;

    get filteredMouvements(): Mouvement[] {
        return this.mouvements.filter(m => {
            const matchType = this.filterType === 'all' || m.type === this.filterType;
            const matchSearch = !this.searchTerm ||
                m.libelle.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                m.compte.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                m.categorie.toLowerCase().includes(this.searchTerm.toLowerCase());
            return matchType && matchSearch;
        });
    }

    get totalCredit(): number {
        return this.mouvements.filter(m => m.type === 'credit').reduce((s, m) => s + m.montant, 0);
    }

    get totalDebit(): number {
        return this.mouvements.filter(m => m.type === 'debit').reduce((s, m) => s + m.montant, 0);
    }

    get solde(): number {
        return this.totalCredit - this.totalDebit;
    }

    submitMouvement(): void {
        this.formSubmitted = true;
        if (!this.newMouvement.libelle || !this.newMouvement.compte || !this.newMouvement.montant || !this.newMouvement.categorie) return;

        // TODO: send to API when business backend is ready
        const entry: Mouvement = {
            id: this.mouvements.length + 1,
            date: this.newMouvement.date,
            libelle: this.newMouvement.libelle,
            type: this.newMouvement.type,
            compte: this.newMouvement.compte,
            montant: this.newMouvement.montant!,
            categorie: this.newMouvement.categorie,
            statut: this.newMouvement.statut
        };
        this.mouvements.unshift(entry);
        this.showSuccess = true;
        this.formSubmitted = false;
        this.resetForm();
        setTimeout(() => { this.showSuccess = false; }, 4000);
    }

    resetForm(): void {
        this.newMouvement = {
            date: new Date().toISOString().slice(0, 10),
            libelle: '',
            type: 'credit',
            compte: '',
            montant: null,
            categorie: '',
            statut: 'en_attente',
            description: ''
        };
    }

    setTab(tab: 'comptabilite' | 'nouveau'): void {
        this.activeTab = tab;
        this.showSuccess = false;
    }

    getCatCredit(cat: string): number {
        return this.mouvements
            .filter(m => m.categorie === cat && m.type === 'credit')
            .reduce((s, m) => s + m.montant, 0);
    }

    getCatDebit(cat: string): number {
        return this.mouvements
            .filter(m => m.categorie === cat && m.type === 'debit')
            .reduce((s, m) => s + m.montant, 0);
    }

    formatAmount(n: number): string {
        return n.toLocaleString('fr-DZ') + ' DZD';
    }
}
