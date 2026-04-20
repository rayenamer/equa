import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, UiInputComponent, UiButtonComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  credentials = {
    email: '',
    password: ''
  };

  onSubmit() {
    console.log('Login attempt:', this.credentials);
    // Logic to call AuthController.signin
  }

  loginWithGoogle() {
    // Logic to call AuthController.getGoogleLoginUrl
    window.location.href = 'http://localhost:8081/oauth2/authorization/google';
  }
}
