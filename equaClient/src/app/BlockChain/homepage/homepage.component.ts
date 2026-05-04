import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { HomeHeaderComponent } from '../../components/organisms/home-header/home-header.component';
import { SiteFooterComponent, FooterLinkGroup } from '../../components/organisms/site-footer/site-footer.component';
import { NavMenuItem } from '../../components/molecules/nav-menu/nav-menu.component';
import { BlockchainSidebarComponent } from '../../components/organisms/blockchain-sidebar/blockchain-sidebar.component';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    HomeHeaderComponent,
    SiteFooterComponent,
    BlockchainSidebarComponent
  ],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.scss'
})
export class HomepageComponent {
  navItems: NavMenuItem[] = [
    { label: 'Dashboard', sectionId: '/blockchain/dashboard' },

    { label: 'Send/Receive', type: 'header' },
    { label: 'Wallets', sectionId: '/blockchain/wallet' },
    { label: 'Transactions', sectionId: '/blockchain/transactions' },
    { label: 'Send', sectionId: '/blockchain/transactions/create' },

    { label: 'System', type: 'header' },
    { label: 'Blocs', sectionId: '/blockchain/blocks' },
    { label: 'Nodes', sectionId: '/blockchain/nodes' },
    { label: 'AI Insights', sectionId: '/blockchain/ai-insights' },
    { label: 'Help', type: 'header' },
    { label: 'How does this work', sectionId: '/blockchain/explanation' }
  ];



  onNavItemClick(path: string): void {
    // In a real app we'd inject Router, but per rules we composition only.
    // However, for navigation to work, we need a way to link.
    // I'll use routerLink in the template instead of this event if possible.
  }
}
