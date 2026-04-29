import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserDTO, UserService } from '../services/user.service';

@Component({
  selector: 'app-users-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users-dashboard.component.html',
  styleUrl: './users-dashboard.component.scss'
})
export class UsersDashboardComponent implements OnInit {
  users: UserDTO[] = [];
  loading = false;
  errorMessage = '';

  stats = {
    total: 0,
    observers: 0,
    assistants: 0,
    activeToday: 0
  };

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.fetchUsers();
  }

  exportUsers(): void {
    const headers = ['id', 'username', 'email', 'userType', 'createdAt'];
    const rows = this.users.map(user => [
      user.id,
      user.username,
      user.email,
      user.userType,
      new Date(user.createdAt).toISOString()
    ]);
    const csvContent = [headers, ...rows]
      .map(row => row.map(value => `"${String(value ?? '').replace(/"/g, '""')}"`).join(','))
      .join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'users-export.csv';
    link.click();
    URL.revokeObjectURL(url);
  }

  private fetchUsers(): void {
    this.loading = true;
    this.errorMessage = '';

    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.computeStats(users);
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Impossible de charger la liste des utilisateurs.';
      }
    });
  }

  private computeStats(users: UserDTO[]): void {
    const now = Date.now();
    const oneDayMs = 24 * 60 * 60 * 1000;
    this.stats.total = users.length;
    this.stats.observers = users.filter(user => user.userType === 'OBSERVER').length;
    this.stats.assistants = users.filter(user => user.userType === 'ASSISTANT').length;
    this.stats.activeToday = users.filter(user => {
      const createdAt = new Date(user.createdAt).getTime();
      return Number.isFinite(createdAt) && now - createdAt <= oneDayMs;
    }).length;
  }
}
