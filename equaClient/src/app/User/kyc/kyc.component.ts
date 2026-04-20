import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';

@Component({
  selector: 'app-kyc',
  standalone: true,
  imports: [CommonModule, FormsModule, UiInputComponent, UiButtonComponent],
  templateUrl: './kyc.component.html',
  styleUrl: './kyc.component.scss'
})
export class KycComponent {
  kycStatus = 'PENDING';
  currentStepIndex = 0;

  kycProfile = {
    nationalIdNumber: '',
    dateOfBirth: '',
    address: '',
    phoneNumber: ''
  };

  kycSteps = [
    { title: 'Informations Personnelles', status: 'current' },
    { title: 'Vérification d\'Identité', status: 'pending' },
    { title: 'Justificatif de Domicile', status: 'pending' },
    { title: 'Évaluation Financière', status: 'pending' }
  ];

  nextStep() {
    if (this.currentStepIndex < this.kycSteps.length - 1) {
      this.kycSteps[this.currentStepIndex].status = 'completed';
      this.currentStepIndex++;
      this.kycSteps[this.currentStepIndex].status = 'current';
    }
  }

  prevStep() {
    if (this.currentStepIndex > 0) {
      this.kycSteps[this.currentStepIndex].status = 'pending';
      this.currentStepIndex--;
      this.kycSteps[this.currentStepIndex].status = 'current';
    }
  }

  onSubmit() {
    console.log('KYC Profile Submission:', this.kycProfile);
    // Call createMyKycProfile
  }
}
