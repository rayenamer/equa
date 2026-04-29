import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { catchError, switchMap } from 'rxjs';
import { AuditLogDTO, UserService } from '../services/user.service';

@Component({
  selector: 'app-audit-logs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './audit-logs.component.html',
  styleUrl: './audit-logs.component.scss'
})
export class AuditLogsComponent {
  logs: Array<{ id: number; action: string; timestamp: Date; details: string }> = [];
  loading = false;
  errorMessage = '';

  constructor(private userService: UserService) {
    this.loadMyAuditLogs();
  }

  private loadMyAuditLogs() {
    this.loading = true;
    this.errorMessage = '';

    this.userService.getCurrentUserId().pipe(
      switchMap((userId) =>
        this.userService.getAuditLogsByObserver(userId).pipe(
          catchError(() => this.userService.getAllAuditLogs())
        )
      )
    ).subscribe({
      next: (logs) => {
        this.logs = logs.map((log) => this.mapAuditLog(log));
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Impossible de charger l\'historique d\'activite.';
      }
    });
  }

  private mapAuditLog(log: AuditLogDTO) {
    return {
      id: log.logId,
      action: log.action,
      timestamp: new Date(log.timestamp),
      details: this.toReadableDetail(log.action)
    };
  }

  private toReadableDetail(action: string): string {
    const value = (action || '').toUpperCase();
    if (value.includes('KYC_APPROVED')) return 'Demande KYC approuvee';
    if (value.includes('KYC_REJECTED')) return 'Demande KYC rejetee';
    if (value.includes('CONNEXION') || value.includes('SIGNIN')) return 'Connexion au compte';
    if (value.includes('MODIF') || value.includes('PROFILE')) return 'Mise a jour du profil';
    if (value.includes('PWD') || value.includes('PASSWORD')) return 'Modification de mot de passe';
    return action;
  }
}
