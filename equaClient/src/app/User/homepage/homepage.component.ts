import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../services/auth.service';
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
  constructor(public authService: AuthService) { }
  navItems: NavMenuItem[] = [
    { label: 'DashBoard', sectionId: '/user/dashboard' },
    { label: 'Profile', sectionId: '/user/profile' },
    { label: 'KYC Center', sectionId: '/user/kyc' },
    { label: 'KYC Management', sectionId: '/user/kyc-management' },
    { label: 'Activity', sectionId: '/user/audit-logs' },
    { label: 'Security', sectionId: '/user/security' }
  ];


}
