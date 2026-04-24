import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';
import { AuthService } from '../services/auth.service';
import { SigninRequestDTO } from '../models/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, UiInputComponent, UiButtonComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  credentials: SigninRequestDTO = {
    email: '',
    password: ''
  };

  loading = false;
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    if (!this.credentials.email || !this.credentials.password) {
      this.errorMessage = 'Veuillez remplir tous les champs.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.signin(this.credentials).subscribe({
      next: (response) => {
        this.loading = false;
        console.log('Login successful:', response);
        // Redirect to dashboard or home
        this.router.navigate(['/']);
      },
      error: (error) => {
        this.loading = false;
        console.error('Login failed:', error);
        this.errorMessage = error.error?.message || 'Erreur de connexion. Vérifiez vos identifiants.';
      }
    });
  }

  loginWithGoogle() {
    // Logic to call AuthController.getGoogleLoginUrl
    window.location.href = 'http://localhost:8081/api/auth/oauth2/google-url';
  }
}
