import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, UiInputComponent, UiButtonComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  user = {
    username: '',
    email: '',
    password: '',
    userType: 'OBSERVER' // Default role
  };

  onSubmit() {
    console.log('Registration attempt:', this.user);
    // Logic to call AuthController.signup
  }
}
