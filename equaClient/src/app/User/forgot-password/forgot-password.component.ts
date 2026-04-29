import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, UiButtonComponent, UiInputComponent],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss'
})
export class ForgotPasswordComponent {
  email = '';
  loading = false;
  message = '';
  errorMessage = '';

  constructor(private authService: AuthService) {}

  onSubmit() {
    if (!this.email) {
      this.errorMessage = 'Veuillez saisir votre email.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.message = '';

    this.authService.forgotPassword(this.email).subscribe({
      next: (response) => {
        this.loading = false;
        this.message = response.message || 'Si un compte existe pour cet email, vous recevrez un lien de réinitialisation.';
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Erreur lors de la demande. Veuillez réessayer.';
      }
    });
  }
}
