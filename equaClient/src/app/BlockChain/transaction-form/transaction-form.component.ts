import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';


@Component({
    selector: 'app-transaction-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './transaction-form.component.html',
    styleUrls: ['./transaction-form.component.scss']
})
export class TransactionFormComponent {
    transactionForm: FormGroup;
    isSubmitting = false;
    successMessage = '';
    errorMessage = '';

    constructor(private fb: FormBuilder, private apiService: ApiService) {
        this.transactionForm = this.fb.group({
            transactionId: [null],
            fromWallet: ['0x12d1cd8f-a2af-44c8-91c9-f59678618884'], // Updated mock data or from auth
            toWallet: ['', [Validators.required]],
            amount: [null, [Validators.required, Validators.min(1)]],
            timestamp: [new Date().toISOString()]
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
