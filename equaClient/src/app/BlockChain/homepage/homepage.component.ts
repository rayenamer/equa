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
    { label: 'Tableau de bord', sectionId: '/blockchain/dashboard' },

    { label: 'Envoi / Réception', type: 'header' },
    { label: 'Portefeuille (Dinar)', sectionId: '/blockchain/wallet' },
    { label: 'Transactions', sectionId: '/blockchain/transactions' },
    { label: 'Envoyer des fonds', sectionId: '/blockchain/transactions/create' },

    { label: 'Système', type: 'header' },
    { label: 'Blocs', sectionId: '/blockchain/blocks' },
    { label: 'Nœuds', sectionId: '/blockchain/nodes' },
    { label: 'IA Insights', sectionId: '/blockchain/ai-insights' },
    { label: 'Comment ça marche', sectionId: '/blockchain/explanation' }
  ];



  onNavItemClick(path: string): void {
    // In a real app we'd inject Router, but per rules we composition only.
    // However, for navigation to work, we need a way to link.
    // I'll use routerLink in the template instead of this event if possible.
  }
}
