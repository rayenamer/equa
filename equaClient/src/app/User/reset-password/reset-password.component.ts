import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, UiButtonComponent, UiInputComponent],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.scss'
})
export class ResetPasswordComponent {
  token = '';
  newPassword = '';
  confirmPassword = '';
  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
  }

  onSubmit() {
    if (!this.token) {
      this.errorMessage = 'Token de réinitialisation manquant.';
      return;
    }

    if (!this.newPassword || !this.confirmPassword) {
      this.errorMessage = 'Veuillez remplir tous les champs.';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Les mots de passe ne correspondent pas.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.resetPassword(this.token, this.newPassword).subscribe({
      next: (response) => {
        this.loading = false;
        this.successMessage = response.message || 'Mot de passe mis à jour.';
        setTimeout(() => this.router.navigate(['/user/login']), 1200);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Réinitialisation impossible. Vérifiez votre lien.';
      }
    });
  }
}
