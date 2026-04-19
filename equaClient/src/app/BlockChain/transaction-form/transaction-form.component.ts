import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

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

    constructor(private fb: FormBuilder) {
        this.transactionForm = this.fb.group({
            transactionId: [null], // To be fed from background
            fromWallet: ['0x71C765...d897'], // Mock session data
            toWallet: ['', [Validators.required, Validators.pattern(/^0x[a-fA-F0-9]{40}$/)]],
            amount: [null, [Validators.required, Validators.min(0.000001)]],
            timestamp: [new Date().toISOString()] // Auto-generate current time
        });
    }

    onSubmit() {
        if (this.transactionForm.valid) {
            this.isSubmitting = true;
            this.successMessage = '';
            this.errorMessage = '';

            // Update timestamp just before submission to be precise
            this.transactionForm.patchValue({ timestamp: new Date().toISOString() });

            // Simulate API call with the full object
            const request = this.transactionForm.value;
            console.log('Sending TransactionRequest to backend:', request);

            setTimeout(() => {
                this.isSubmitting = false;
                this.successMessage = 'Transaction envoyée avec succès au réseau !';
                this.transactionForm.reset({
                    fromWallet: '0x71C765...d897'
                });
            }, 1500);
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
