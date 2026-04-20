import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { NavMenuItem } from '../../components/molecules/nav-menu/nav-menu.component';
import { BlockchainSidebarComponent } from '../../components/organisms/blockchain-sidebar/blockchain-sidebar.component';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, BlockchainSidebarComponent],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.scss'
})
export class HomepageComponent {
  navItems: NavMenuItem[] = [
    { label: 'Tableau de bord', sectionId: '/user/dashboard' },
    { label: 'Profil', sectionId: '/user/profile' },
    { label: 'Centre KYC', sectionId: '/user/kyc' },
    { label: 'Gestion KYC', sectionId: '/user/kyc-management' },
    { label: 'Activité', sectionId: '/user/audit-logs' },
    { label: 'Sécurité', sectionId: '/user/security' }
  ];


}
