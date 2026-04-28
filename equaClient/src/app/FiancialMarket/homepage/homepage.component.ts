import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { HomeHeaderComponent } from '../../components/organisms/home-header/home-header.component';
import { SiteFooterComponent } from '../../components/organisms/site-footer/site-footer.component';
import { BlockchainSidebarComponent } from '../../components/organisms/blockchain-sidebar/blockchain-sidebar.component';
import { NavMenuItem } from '../../components/molecules/nav-menu/nav-menu.component';

@Component({
  selector: 'app-homepage',
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
    { label: 'Vue d\'ensemble', sectionId: '/financial-market/dashboard' },

    { label: 'Investir', type: 'header' },
    { label: 'Marché des Actifs', sectionId: '/financial-market/assets' },
    { label: 'Mon Portefeuille', sectionId: '/financial-market/portfolio' },

    { label: 'Gérer', type: 'header' },
    { label: 'Créer un Actif', sectionId: '/financial-market/assets/create' },

    { label: 'Aide', type: 'header' },
    { label: 'Comment ça marche', sectionId: '/financial-market/explanation' }
  ];



  onNavItemClick(path: string): void {
    // In a real app we'd inject Router, but per rules we composition only.
    // However, for navigation to work, we need a way to link.
    // I'll use routerLink in the template instead of this event if possible.
  }

}
