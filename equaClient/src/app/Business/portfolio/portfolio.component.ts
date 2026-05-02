import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WalletCardComponent } from '../../components/wallet-card/wallet-card.component';
import { ApiService } from '../../services/api.service';
import { WalletDTO } from '../../BlockChain/models/dinar-wallet.model';
import { catchError, of } from 'rxjs';

@Component({
    selector: 'app-business-portfolio',
    standalone: true,
    imports: [CommonModule, FormsModule, WalletCardComponent],
    templateUrl: './portfolio.component.html',
    styleUrl: './portfolio.component.scss'
})
export class BusinessPortfolioComponent implements OnInit {
    equaWallet: WalletDTO | null = null;
    walletExists = false;
    showSendModal = false;
    currentBusinessId: number | null = null;

    sendForm = { recipientId: '', montant: null as number | null };

    constructor(private apiService: ApiService) { }

    ngOnInit(): void {
        this.apiService.getBusinesses().subscribe({
            next: (businesses) => {
                if (businesses && businesses.length > 0) {
                    this.currentBusinessId = businesses[0].id;
                    this.apiService.getBusinessWallet(this.currentBusinessId!).pipe(
                        catchError(() => of(null))
                    ).subscribe(wallet => {
                        this.equaWallet = wallet;
                        this.walletExists = !!wallet;
                    });
                }
            }
        });
    }

    createEquaWallet(): void {
        if (!this.currentBusinessId) return;
        this.apiService.createBusinessWallet(this.currentBusinessId).subscribe(wallet => {
            this.equaWallet = wallet;
            this.walletExists = true;
        });
    }

    closeSend(): void {
        this.showSendModal = false;
        this.sendForm = { recipientId: '', montant: null };
    }

    confirmSend(): void {
        if (!this.sendForm.recipientId || !this.sendForm.montant) return;
        // TODO: wire to blockchain transaction API
        alert(`Envoi de ${this.sendForm.montant} EQUA vers ${this.sendForm.recipientId}`);
        this.closeSend();
    }
}
