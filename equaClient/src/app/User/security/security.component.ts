import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-security',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './security.component.html',
  styleUrl: './security.component.scss'
})
export class SecurityComponent {
  twoFactorEnabled = false;
  passwordForm = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
  passwordLoading = false;
  passwordSuccessMessage = '';
  passwordErrorMessage = '';

  disableLoading = false;
  disableSuccessMessage = '';
  disableErrorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  updatePassword() {
    const { currentPassword, newPassword, confirmPassword } = this.passwordForm;
    this.passwordSuccessMessage = '';
    this.passwordErrorMessage = '';

    if (!currentPassword || !newPassword || !confirmPassword) {
      this.passwordErrorMessage = 'Veuillez remplir tous les champs.';
      return;
    }
    if (newPassword !== confirmPassword) {
      this.passwordErrorMessage = 'Le nouveau mot de passe et sa confirmation ne correspondent pas.';
      return;
    }
    if (newPassword.length < 6) {
      this.passwordErrorMessage = 'Le nouveau mot de passe doit contenir au moins 6 caracteres.';
      return;
    }

    this.passwordLoading = true;
    this.authService.changePassword(currentPassword, newPassword).subscribe({
      next: (response) => {
        this.passwordLoading = false;
        this.passwordSuccessMessage = response.message || 'Mot de passe mis a jour avec succes.';
        this.passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };
      },
      error: (error) => {
        this.passwordLoading = false;
        this.passwordErrorMessage =
          error.error?.message || 'Impossible de modifier le mot de passe pour le moment.';
      }
    });
  }

  disableAccount() {
    const confirmed = window.confirm(
      'Cette action est irreversible. Voulez-vous vraiment desactiver votre compte ?'
    );
    if (!confirmed) return;

    this.disableLoading = true;
    this.disableSuccessMessage = '';
    this.disableErrorMessage = '';

    this.authService.disableAccount().subscribe({
      next: (response) => {
        this.disableLoading = false;
        this.disableSuccessMessage = response.message || 'Compte desactive avec succes.';
        this.authService.logout();
        setTimeout(() => this.router.navigate(['/user/login']), 1200);
      },
      error: (error) => {
        this.disableLoading = false;
        this.disableErrorMessage =
          error.error?.message || 'Impossible de desactiver le compte pour le moment.';
      }
    });
  }
}
