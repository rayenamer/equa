import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-audit-logs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './audit-logs.component.html',
  styleUrl: './audit-logs.component.scss'
})
export class AuditLogsComponent {
  logs = [
    { id: 1, action: 'CONNEXION', timestamp: new Date(), details: 'Connexion réussie depuis 192.168.1.1' },
    { id: 2, action: 'MODIF_PROFIL', timestamp: new Date(Date.now() - 3600000), details: 'Changement d\'adresse email' },
    { id: 3, action: 'KYC_SOUMISSION', timestamp: new Date(Date.now() - 86400000), details: 'Documents d\'identité envoyés' },
    { id: 4, action: 'CHANGE_PWD', timestamp: new Date(Date.now() - 172800000), details: 'Mot de passe mis à jour' }
  ];
}
