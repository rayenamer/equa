import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { HomeHeaderComponent } from '../../components/organisms/home-header/home-header.component';
import { SiteFooterComponent, FooterLinkGroup } from '../../components/organisms/site-footer/site-footer.component';
import { NavMenuItem } from '../../components/molecules/nav-menu/nav-menu.component';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    HomeHeaderComponent,
    SiteFooterComponent
  ],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.scss'
})
export class HomepageComponent {
  navItems: NavMenuItem[] = [
    { label: 'Tableau de bord', sectionId: '/blockchain/dashboard' },
    { label: 'Transactions', sectionId: '/blockchain/transactions' },
    { label: 'Envoyer des fonds', sectionId: '/blockchain/transactions/create' },
    { label: 'Blocs', sectionId: '/blockchain/blocks' },
    { label: 'Nœuds', sectionId: '/blockchain/nodes' },
    { label: 'Portefeuille (Dinar)', sectionId: '/blockchain/wallet' },
    { label: 'IA Insights', sectionId: '/blockchain/ai-insights' }
  ];

  footerGroups: FooterLinkGroup[] = [
    {
      title: 'Ecosystème',
      links: [
        { label: 'Explorateur', href: '#' },
        { label: 'Validateurs', href: '#' },
        { label: 'Dinar Stable', href: '#' }
      ]
    },
    {
      title: 'Ressources',
      links: [
        { label: 'Documentation API', href: '#' },
        { label: 'Whitepaper', href: '#' },
        { label: 'GitHub', href: '#' }
      ]
    }
  ];

  onNavItemClick(path: string): void {
    // In a real app we'd inject Router, but per rules we composition only.
    // However, for navigation to work, we need a way to link.
    // I'll use routerLink in the template instead of this event if possible.
  }
}
