import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, UiInputComponent, UiButtonComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {
  user = {
    username: 'johndoe',
    email: 'john.doe@example.com',
    userType: 'OBSERVER',
    riskLevel: 'LOW',
    kycStatus: 'APPROVED',
    memberSince: '2024-01-15',
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
    console.log('Saving profile:', this.user);
    this.isEditing = false;
    // Logic to call UserController.updateUser and updateUserRoles
  }
}
