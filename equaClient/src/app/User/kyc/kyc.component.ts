import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';
import { KycProfileDTO, UserService } from '../services/user.service';

@Component({
  selector: 'app-kyc',
  standalone: true,
  imports: [CommonModule, FormsModule, UiInputComponent, UiButtonComponent],
  templateUrl: './kyc.component.html',
  styleUrl: './kyc.component.scss'
})
export class KycComponent implements OnInit {
  kycStatus = 'PENDING';
  currentStepIndex = 0;
  loading = false;
  saving = false;
  errorMessage = '';
  successMessage = '';
  private hasExistingProfile = false;

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

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadMyKycProfile();
  }

  nextStep() {
    if (this.currentStepIndex === 0 && !this.isPersonalInfoValid()) {
      this.errorMessage = 'Veuillez remplir toutes les informations personnelles.';
      return;
    }
    this.errorMessage = '';
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
    if (!this.isPersonalInfoValid()) {
      this.errorMessage = 'Veuillez remplir toutes les informations personnelles.';
      return;
    }

    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const normalizedDate = this.normalizeDateForBackend(this.kycProfile.dateOfBirth);
    if (!normalizedDate) {
      this.errorMessage = 'Format de date invalide. Utilisez AAAA-MM-JJ ou JJ/MM/AAAA.';
      return;
    }

    const payload: KycProfileDTO = {
      nationalIdNumber: this.kycProfile.nationalIdNumber.trim(),
      dateOfBirth: normalizedDate,
      address: this.kycProfile.address.trim(),
      phoneNumber: this.kycProfile.phoneNumber.trim()
    };

    const request$ = this.hasExistingProfile
      ? this.userService.updateMyKycProfile(payload)
      : this.userService.createMyKycProfile(payload);

    request$.subscribe({
      next: (profile) => {
        this.hasExistingProfile = true;
        this.kycStatus = 'PENDING';
        this.saving = false;
        this.successMessage = 'Profil KYC enregistre avec succes. Il est en attente de validation.';
        if (profile.dateOfBirth) {
          this.kycProfile.dateOfBirth = profile.dateOfBirth;
        }
      },
      error: (error) => {
        this.saving = false;
        this.errorMessage = error.error?.message || 'Echec de soumission du profil KYC.';
      }
    });
  }

  private loadMyKycProfile() {
    this.loading = true;
    this.errorMessage = '';

    this.userService.getMyKycProfile().subscribe({
      next: (profile) => {
        this.kycProfile.nationalIdNumber = profile.nationalIdNumber ?? '';
        this.kycProfile.dateOfBirth = profile.dateOfBirth ?? '';
        this.kycProfile.address = profile.address ?? '';
        this.kycProfile.phoneNumber = profile.phoneNumber ?? '';
        this.hasExistingProfile = !!profile.kycSubmittedAt;
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        const message = error.error?.message || '';
        if (message.includes('OBSERVER')) {
          this.errorMessage = 'La section KYC est disponible uniquement pour les comptes OBSERVER.';
          return;
        }
        this.hasExistingProfile = false;
      }
    });
  }

  private isPersonalInfoValid(): boolean {
    return !!(
      this.kycProfile.nationalIdNumber.trim() &&
      this.kycProfile.dateOfBirth &&
      this.kycProfile.address.trim() &&
      this.kycProfile.phoneNumber.trim()
    );
  }

  private normalizeDateForBackend(rawDate: string): string | null {
    const value = (rawDate || '').trim();
    if (!value) return null;

    // Already ISO yyyy-MM-dd
    if (/^\d{4}-\d{2}-\d{2}$/.test(value)) return value;

    // Accept dd/MM/yyyy from user input
    const slashFormat = value.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
    if (slashFormat) {
      const [, day, month, year] = slashFormat;
      return `${year}-${month}-${day}`;
    }

    return null;
  }
}
