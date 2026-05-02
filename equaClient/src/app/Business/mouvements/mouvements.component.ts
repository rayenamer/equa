import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { TransactionService } from '../../BlockChain/services/transaction.service';
import { Transaction } from '../../BlockChain/models/transaction.model';

export interface Mouvement {
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
    selector: 'app-mouvements',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './mouvements.component.html',
    styleUrl: './mouvements.component.scss'
})
export class MouvementsComponent implements OnInit {
    activeTab: 'comptabilite' | 'nouveau' | 'transactions' = 'comptabilite';
    filterType: 'all' | 'sortant' | 'entrant' = 'all';
    searchTerm = '';

    // ─── Real data for analytical accounting ──────────────────────────────────
    mouvements: Mouvement[] = [];
    currentBusinessId: number | null = null;
    isLoading = false;

    // ─── Wallet transactions ───────────────────────────────────────────────────
    walletTransactions: Transaction[] = [];
    myWallet: any = null;
    myWalletId: string = '';

    // ─── New movement form ────────────────────────────────────────────────────
    newMouvement = {
        date: new Date().toISOString().slice(0, 10),
        libelle: '',
        type: 'entrant' as 'entrant' | 'sortant',
        compte: '',
        montant: null as number | null,
        categorie: '',
        statut: 'en_attente' as 'valide' | 'en_attente' | 'annule',
        description: ''
    };

    comptes = [
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
        'Exceptionnel',
    ];

    formSubmitted = false;
    showSuccess = false;

    constructor(private apiService: ApiService, private transactionService: TransactionService) { }

    ngOnInit(): void {
        this.loadBusinessContext();
    }

    loadBusinessContext(): void {
        this.isLoading = true;
        this.apiService.getBusinesses().subscribe({
            next: (businesses) => {
                if (businesses && businesses.length > 0) {
                    this.currentBusinessId = businesses[0].id;
                    this.loadMouvements();
                    this.loadWalletTransactions();
                }
            },
            error: (err) => {
                console.error('Error loading business', err);
                this.isLoading = false;
            }
        });
    }

    loadMouvements(): void {
        if (!this.currentBusinessId) return;
        this.apiService.getMouvements(this.currentBusinessId).subscribe({
            next: (data) => {
                this.mouvements = data;
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Error loading mouvements', err);
                this.isLoading = false;
            }
        });
    }

    loadWalletTransactions(): void {
        this.apiService.getMyWallet().subscribe({
            next: (wallet) => {
                if (wallet) {
                    this.myWallet = wallet;
                    const identifier = wallet.publicKey || (wallet as any).walletId || (wallet.id ? wallet.id.toString() : '');
                    this.myWalletId = identifier;

                    this.transactionService.getAllTransactions().subscribe({
                        next: (txs) => {
                            const userTxs = txs.filter(tx => tx.fromWallet === identifier || tx.toWallet === identifier);
                            this.walletTransactions = userTxs.length > 0 ? userTxs : txs;
                        },
                        error: (err) => console.error('Error loading transactions', err)
                    });
                }
            },
            error: (err) => console.error('Error loading wallet', err)
        });
    }

    get pendingMouvements(): Mouvement[] {
        return this.mouvements.filter(m => m.statut === 'en_attente');
    }

    get filteredMouvements(): Mouvement[] {
        return this.mouvements.filter(m => {
            if (m.statut === 'en_attente') return false; // Show only non-pending here

            const matchType = this.filterType === 'all' || m.type === this.filterType;
            const matchSearch = !this.searchTerm ||
                m.libelle.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                m.compte.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                m.categorie.toLowerCase().includes(this.searchTerm.toLowerCase());
            return matchType && matchSearch;
        });
    }

    get totalCredit(): number {
        return this.mouvements.filter(m => m.type === 'entrant' && m.statut !== 'en_attente').reduce((s, m) => s + m.montant, 0);
    }

    get totalDebit(): number {
        return this.mouvements.filter(m => m.type === 'sortant' && m.statut !== 'en_attente').reduce((s, m) => s + m.montant, 0);
    }

    get solde(): number {
        return this.totalCredit - this.totalDebit;
    }

    submitMouvement(): void {
        this.formSubmitted = true;
        if (!this.newMouvement.libelle || !this.newMouvement.compte || !this.newMouvement.montant || !this.newMouvement.categorie) return;
        if (!this.currentBusinessId) return;

        const payload = {
            date: this.newMouvement.date,
            libelle: this.newMouvement.libelle,
            type: this.newMouvement.type,
            compte: this.newMouvement.compte,
            montant: this.newMouvement.montant,
            categorie: this.newMouvement.categorie,
            statut: this.newMouvement.statut
        };

        this.apiService.addMouvement(this.currentBusinessId, payload).subscribe({
            next: (savedMouvement) => {
                this.mouvements.unshift(savedMouvement);
                this.showSuccess = true;
                this.formSubmitted = false;
                this.resetForm();
                setTimeout(() => { this.showSuccess = false; }, 4000);
            },
            error: (err) => console.error('Error adding mouvement', err)
        });
    }

    resetForm(): void {
        this.newMouvement = {
            date: new Date().toISOString().slice(0, 10),
            libelle: '',
            type: 'entrant',
            compte: '',
            montant: null,
            categorie: '',
            statut: 'en_attente',
            description: ''
        };
    }

    setTab(tab: 'comptabilite' | 'nouveau' | 'transactions'): void {
        this.activeTab = tab;
        this.showSuccess = false;
    }

    isSortant(tx: Transaction): boolean {
        if (!tx || !this.myWallet) return false;
        const fromW = String(tx.fromWallet).toLowerCase().trim();
        const pk = this.myWallet.publicKey ? String(this.myWallet.publicKey).toLowerCase().trim() : '';
        const wid = (this.myWallet as any).walletId ? String((this.myWallet as any).walletId).toLowerCase().trim() : '';
        const idStr = this.myWallet.id ? String(this.myWallet.id).toLowerCase().trim() : '';
        return fromW === pk || fromW === wid || fromW === idStr;
    }

    importTransaction(tx: Transaction): void {
        if (!this.currentBusinessId) return;

        const type = this.isSortant(tx) ? 'sortant' : 'entrant';
        const dateStr = new Date(tx.timestamp).toISOString().slice(0, 10);

        const payload = {
            date: dateStr,
            libelle: `Transaction TX-${tx.transactionHash ? tx.transactionHash.substring(0, 8) : '000'}`,
            type: type,
            compte: 'Transaction Blockchain',
            montant: tx.amount,
            categorie: 'Exceptionnel',
            statut: 'en_attente'
        };

        this.apiService.addMouvement(this.currentBusinessId, payload).subscribe({
            next: (savedMouvement) => {
                this.mouvements.unshift(savedMouvement);
                this.showSuccess = true;
                setTimeout(() => { this.showSuccess = false; }, 4000);
            },
            error: (err) => console.error('Error importing transaction as mouvement', err)
        });
    }

    getCatCredit(cat: string): number {
        return this.mouvements
            .filter(m => m.categorie === cat && m.type === 'entrant' && m.statut !== 'en_attente')
            .reduce((s, m) => s + m.montant, 0);
    }

    getCatDebit(cat: string): number {
        return this.mouvements
            .filter(m => m.categorie === cat && m.type === 'sortant' && m.statut !== 'en_attente')
            .reduce((s, m) => s + m.montant, 0);
    }

    // Modal state for classification
    selectedMovementToClassify: Mouvement | null = null;
    classificationForm = {
        compte: '',
        categorie: ''
    };

    openClassifyModal(id: number): void {
        const m = this.mouvements.find(x => x.id === id);
        if (m) {
            this.selectedMovementToClassify = m;
            this.classificationForm = {
                compte: '',
                categorie: ''
            };
        }
    }

    cancelClassification(): void {
        this.selectedMovementToClassify = null;
    }

    confirmClassification(): void {
        if (!this.selectedMovementToClassify || !this.classificationForm.compte || !this.classificationForm.categorie) return;

        this.apiService.classifyMouvement(
            this.selectedMovementToClassify.id,
            'valide',
            this.classificationForm.compte,
            this.classificationForm.categorie
        ).subscribe({
            next: (updatedMouvement) => {
                const idx = this.mouvements.findIndex(m => m.id === updatedMouvement.id);
                if (idx !== -1) {
                    this.mouvements[idx] = updatedMouvement;
                }
                this.mouvements = [...this.mouvements];
                this.selectedMovementToClassify = null;
            },
            error: (err) => console.error('Error classifying', err)
        });
    }

    formatAmount(n: number): string {
        return n.toLocaleString('fr-DZ') + ' EQUA';
    }
}
