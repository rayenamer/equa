import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';
import { AuthService } from '../services/auth.service';
import { SignupRequestDTO } from '../models/auth.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, UiInputComponent, UiButtonComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  user: SignupRequestDTO = {
    username: '',
    email: '',
    password: '',
    userType: 'OBSERVER' // Default role
  };

  loading = false;
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    if (!this.user.username || !this.user.email || !this.user.password) {
      this.errorMessage = 'Veuillez remplir tous les champs.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.signup(this.user).subscribe({
      next: (response) => {
        this.loading = false;
        console.log('Registration successful:', response);
        // Redirect to login or auto-login
        this.router.navigate(['/user/login']);
      },
      error: (error) => {
        this.loading = false;
        console.error('Registration failed:', error);
        this.errorMessage = error.error?.message || 'Erreur lors de l\'inscription.';
      }
    });
  }
}
