import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UiButtonComponent } from '../../components/atoms/ui-button/ui-button.component';
import { UiInputComponent } from '../../components/atoms/ui-input/ui-input.component';

@Component({
  selector: 'app-kyc-management',
  standalone: true,
  imports: [CommonModule, FormsModule, UiButtonComponent, UiInputComponent],
  templateUrl: './kyc-management.component.html',
  styleUrl: './kyc-management.component.scss'
})
export class KycManagementComponent {
  users = [
    { id: 1, username: 'johndoe', email: 'john@example.com', kycStatus: 'PENDING', lastSubmitted: new Date() },
    { id: 2, username: 'janedoe', email: 'jane@example.com', kycStatus: 'APPROVED', lastSubmitted: new Date(Date.now() - 86400000) },
    { id: 3, username: 'bobsmith', email: 'bob@example.com', kycStatus: 'REJECTED', lastSubmitted: new Date(Date.now() - 172800000) },
    { id: 4, username: 'alice_w', email: 'alice@example.com', kycStatus: 'PENDING', lastSubmitted: new Date() }
  ];

  selectedUser: any = null;
  reviewNote = '';

  openReview(user: any) {
    this.selectedUser = user;
    this.reviewNote = '';
  }

  updateStatus(status: 'APPROVED' | 'REJECTED') {
    if (!this.selectedUser) return;
    
    console.log(`Updating KYC for ${this.selectedUser.username} to ${status} with note: ${this.reviewNote}`);
    
    // Logic to call UserController.updateObserverKycStatus
    this.selectedUser.kycStatus = status;
    this.selectedUser = null;
  }
}
