import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';
import { UserDTO, UserService } from '../services/user.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, UiInputComponent, UiButtonComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {
  user = {
    id: 0,
    username: '',
    email: '',
    userType: 'OBSERVER',
    riskLevel: 'LOW',
    kycStatus: 'PENDING',
    createdAt: '',
    updatedAt: '',
    permissions: ['VIEW_LOGS', 'AUDIT']
  };

  availablePermissions = [
    { id: 'VIEW_LOGS', label: 'Consulter les logs' },
    { id: 'AUDIT', label: 'Effectuer des audits' },
    { id: 'MANAGE_USERS', label: 'Gérer les utilisateurs' },
    { id: 'MONITOR_TRANSACTIONS', label: 'Surveiller la blockchain' },
    { id: 'VIEW_REPORTS', label: 'Voir les rapports' },
    { id: 'APPROVE_LOANS', label: 'Approuver des prêts' }
  ];

  isEditing = false;
  loading = false;
  saving = false;
  errorMessage = '';
  successMessage = '';

  constructor(private userService: UserService) {
    this.loadProfile();
  }

  togglePermission(permId: string) {
    if (this.user.userType !== 'OBSERVER') return;
    
    const index = this.user.permissions.indexOf(permId);
    if (index > -1) {
      this.user.permissions.splice(index, 1);
    } else {
      this.user.permissions.push(permId);
    }
  }

  hasPermission(permId: string): boolean {
    return this.user.permissions.includes(permId);
  }

  saveProfile() {
    if (!this.user.id) return;

    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const userPayload: UserDTO = {
      id: this.user.id,
      username: this.user.username,
      email: this.user.email,
      userType: this.user.userType,
      createdAt: this.user.createdAt,
      updatedAt: this.user.updatedAt
    };

    const rolesPayload = {
      userType: this.user.userType,
      permissions: this.user.userType === 'OBSERVER' ? this.user.permissions : []
    };

    forkJoin([
      this.userService.updateUser(this.user.id, userPayload),
      this.userService.updateUserRoles(this.user.id, rolesPayload)
    ]).subscribe({
      next: ([updatedUser, updatedRoles]) => {
        this.user.username = updatedUser.username;
        this.user.email = updatedUser.email;
        this.user.userType = updatedRoles.userType;
        this.user.permissions = updatedRoles.permissions ?? [];
        this.saving = false;
        this.successMessage = 'Profil mis a jour avec succes.';
      },
      error: (error) => {
        this.saving = false;
        this.errorMessage = error.error?.message || 'Echec de mise a jour du profil.';
      }
    });
  }

  private loadProfile() {
    this.loading = true;
    this.errorMessage = '';

    this.userService.getCurrentUserId().subscribe({
      next: (id) => {
        forkJoin([
          this.userService.getUserById(id),
          this.userService.getUserRoles(id),
          this.userService.getRiskLevel(id),
          this.userService.getKycStatus(id)
        ]).subscribe({
          next: ([user, roles, risk, kyc]) => {
            this.user.id = user.id;
            this.user.username = user.username;
            this.user.email = user.email;
            this.user.userType = roles.userType || user.userType;
            this.user.createdAt = user.createdAt;
            this.user.updatedAt = user.updatedAt;
            this.user.permissions = roles.permissions ?? [];
            this.user.riskLevel = risk.riskLevel ?? 'LOW';
            this.user.kycStatus = kyc.kycStatus ?? 'PENDING';
            this.loading = false;
          },
          error: (error) => {
            this.loading = false;
            this.errorMessage = error.error?.message || 'Impossible de charger le profil.';
          }
        });
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Utilisateur non identifie.';
      }
    });
  }
}
