import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { WalletDTO } from '../models/dinar-wallet.model';


@Component({
    selector: 'app-transaction-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './transaction-form.component.html',
    styleUrls: ['./transaction-form.component.scss']
})
export class TransactionFormComponent implements OnInit {
    transactionForm: FormGroup;
    isSubmitting = false;
    successMessage = '';
    errorMessage = '';
    myWallet: WalletDTO | null = null;

    constructor(private fb: FormBuilder, private apiService: ApiService) {
        this.transactionForm = this.fb.group({
            transactionId: [null],
            fromWallet: ['', [Validators.required]],
            toWallet: ['', [Validators.required]],
            amount: [null, [Validators.required, Validators.min(0.000001)]],
            timestamp: [new Date().toISOString()]
        });
    }

    ngOnInit() {
        this.loadMyWallet();
    }

    loadMyWallet() {
        this.apiService.getMyWallet().subscribe({
            next: (wallet) => {
                this.myWallet = wallet;
                this.transactionForm.patchValue({
                    fromWallet: wallet.id.toString()
                });
            },
            error: (error) => {
                console.error('Error fetching wallet:', error);
                this.errorMessage = 'Impossible de charger votre portefeuille. Vérifiez que vous en possédez un.';
            }
        });
    }

    onSubmit() {
        if (this.transactionForm.valid) {
            this.isSubmitting = true;
            this.successMessage = '';
            this.errorMessage = '';

            const request = {
                ...this.transactionForm.value,
                timestamp: new Date().toISOString()
            };

            // Force cast to string for the API
            request.fromWallet = request.fromWallet.toString();
            request.toWallet = request.toWallet.toString();

            this.apiService.processTransaction(request).subscribe({
                next: (response) => {
                    this.isSubmitting = false;
                    this.successMessage = `Transaction ${response.transactionHash} envoyée avec succès !`;
                    this.transactionForm.reset({
                        fromWallet: request.fromWallet
                    });
                },
                error: (error) => {
                    this.isSubmitting = false;
                    this.errorMessage = 'Erreur lors du traitement de la transaction: ' + error.message;
                }
            });
        } else {
            this.errorMessage = 'Veuillez remplir tous les champs correctement.';
            this.markFormGroupTouched(this.transactionForm);
        }
    }


    private markFormGroupTouched(formGroup: FormGroup) {
        Object.values(formGroup.controls).forEach(control => {
            control.markAsTouched();
            if ((control as any).controls) {
                this.markFormGroupTouched(control as FormGroup);
            }
        });
    }
}
