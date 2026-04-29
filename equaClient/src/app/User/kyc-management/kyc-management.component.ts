import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';
import { KycStatus, UserDTO, UserService } from '../services/user.service';

@Component({
  selector: 'app-kyc-management',
  standalone: true,
  imports: [CommonModule, FormsModule, UiButtonComponent, UiInputComponent],
  templateUrl: './kyc-management.component.html',
  styleUrl: './kyc-management.component.scss'
})
export class KycManagementComponent {
  users: Array<UserDTO & { kycStatus: KycStatus; displayStatus: string; lastSubmitted: Date }> = [];
  loading = false;
  actionLoading = false;
  errorMessage = '';
  successMessage = '';

  selectedUser: (UserDTO & { kycStatus: KycStatus; displayStatus: string; lastSubmitted: Date }) | null = null;
  reviewNote = '';

  constructor(private userService: UserService) {
    this.loadKycUsers();
  }

  openReview(user: UserDTO & { kycStatus: KycStatus; displayStatus: string; lastSubmitted: Date }) {
    this.selectedUser = user;
    this.reviewNote = '';
    this.successMessage = '';
  }

  updateStatus(status: 'APPROVED' | 'REJECTED') {
    if (!this.selectedUser) return;

    this.actionLoading = true;
    this.errorMessage = '';

    const backendStatus = status === 'APPROVED' ? 'VERIFIED' : 'REJECTED';
    this.userService.updateObserverKycStatus(this.selectedUser.id, backendStatus, this.reviewNote.trim()).subscribe({
      next: (response) => {
        this.selectedUser!.kycStatus = response.kycStatus || backendStatus;
        this.selectedUser!.displayStatus = this.toDisplayStatus(this.selectedUser!.kycStatus);
        this.successMessage = `Statut KYC mis a jour pour ${this.selectedUser!.username}.`;
        this.actionLoading = false;
        this.selectedUser = null;
      },
      error: (error) => {
        this.actionLoading = false;
        this.errorMessage = error.error?.message || 'Echec de mise a jour du statut KYC.';
      }
    });
  }

  private loadKycUsers() {
    this.loading = true;
    this.errorMessage = '';

    this.userService.getAllUsers().subscribe({
      next: (users) => {
        const observerUsers = users.filter(user => user.userType === 'OBSERVER');
        if (!observerUsers.length) {
          this.users = [];
          this.loading = false;
          return;
        }

        const statusCalls = observerUsers.map(user => this.userService.getKycStatus(user.id));
        forkJoin(statusCalls).subscribe({
          next: (statuses) => {
            this.users = observerUsers.map((user, index) => ({
              ...user,
              kycStatus: statuses[index]?.kycStatus ?? 'PENDING',
              displayStatus: this.toDisplayStatus(statuses[index]?.kycStatus ?? 'PENDING'),
              lastSubmitted: new Date((user as any).kycSubmittedAt || user.updatedAt || user.createdAt)
            }));
            this.loading = false;
          },
          error: () => {
            // Fallback: show users even if KYC status endpoint fails for some
            this.users = observerUsers.map(user => ({
              ...user,
              kycStatus: 'PENDING',
              displayStatus: this.toDisplayStatus('PENDING'),
              lastSubmitted: new Date((user as any).kycSubmittedAt || user.updatedAt || user.createdAt)
            }));
            this.loading = false;
          }
        });
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Impossible de charger la liste KYC.';
      }
    });
  }

  private toDisplayStatus(status: string): string {
    const value = (status || '').toUpperCase();
    if (value === 'VERIFIED') return 'APPROVED';
    return value || 'PENDING';
  }
}
