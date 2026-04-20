import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-users-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users-dashboard.component.html',
  styleUrl: './users-dashboard.component.scss'
})
export class UsersDashboardComponent {
  users = [
    { id: 1, username: 'admin', email: 'admin@equa.io', userType: 'ASSISTANT', createdAt: new Date() },
    { id: 2, username: 'johndoe', email: 'john@example.com', userType: 'OBSERVER', createdAt: new Date(Date.now() - 86400000) },
    { id: 3, username: 'janedoe', email: 'jane@example.com', userType: 'OBSERVER', createdAt: new Date(Date.now() - 172800000) },
    { id: 4, username: 'support_ref', email: 'support@equa.io', userType: 'ASSISTANT', createdAt: new Date(Date.now() - 259200000) }
  ];

  stats = {
    total: 124,
    observers: 98,
    assistants: 26,
    activeToday: 12
  };
}
