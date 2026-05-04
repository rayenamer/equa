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
    { label: 'Dashboard', sectionId: '/financial-market/dashboard' },

    { label: 'Invest', type: 'header' },
    { label: 'Market', sectionId: '/financial-market/assets' },
    { label: 'My Portfolio', sectionId: '/financial-market/portfolio' },

    { label: 'Manage', type: 'header' },
    { label: 'Create', sectionId: '/financial-market/assets/create' },

    { label: 'Help', type: 'header' },
    { label: 'How does this work', sectionId: '/financial-market/explanation' }
  ];



  onNavItemClick(path: string): void {
    // In a real app we'd inject Router, but per rules we composition only.
    // However, for navigation to work, we need a way to link.
    // I'll use routerLink in the template instead of this event if possible.
  }

}
