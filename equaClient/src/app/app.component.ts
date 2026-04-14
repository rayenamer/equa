import { Component } from '@angular/core';
import { OutletContext, RouterOutlet } from '@angular/router';
import { SplashCursorComponent } from './components/splash-cursor/splash-cursor.component';
import { HomeHeaderComponent } from './components/organisms/home-header/home-header.component';

import { NavMenuItem } from './components/molecules/nav-menu/nav-menu.component';
import { SiteFooterComponent } from './components/organisms/site-footer/site-footer.component';

import { FooterLinkGroup } from './components/organisms/site-footer/site-footer.component';
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SplashCursorComponent,HomeHeaderComponent,SiteFooterComponent],
  templateUrl: './app.component.html',
  styles: []
})
export class AppComponent {
  title = 'EQUA - Finance Without Barriers';
    headerNavItems: NavMenuItem[] = [
      { label: 'Accueil', sectionId: 'token-intro' },
      { label: 'Token', sectionId: 'token-intro' },
      { label: 'Services', sectionId: 'how-it-works' },
      { label: 'FAQ', sectionId: 'faq' },
      { label: 'Contact', sectionId: 'contact-cta' }
    ];
    mobileMenuOpen = false;
    toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }
  scrollTo(id: string): void {
    this.mobileMenuOpen = false;
    const el = document.getElementById(id);
    el?.scrollIntoView({ behavior: 'smooth' });
  }
    footerSocialLinks = [
    { label: 'Twitter', href: '#', ariaLabel: 'Twitter' },
    { label: 'LinkedIn', href: '#', ariaLabel: 'LinkedIn' },
    { label: 'Discord', href: '#', ariaLabel: 'Discord' },
    { label: 'Telegram', href: '#', ariaLabel: 'Telegram' }
  ];
   footerLinkGroups: FooterLinkGroup[] = [
      {
        title: 'Produit',
        links: [
          { label: 'Token', href: '#' },
          { label: 'Services', href: '#' },
          { label: 'Documentation', href: '#' }
        ]
      },
      {
        title: 'Ressources',
        links: [
          { label: 'Blog', href: '#' },
          { label: 'FAQ', href: '#' },
          { label: 'Support', href: '#' }
        ]
      },
      {
        title: 'Légal',
        links: [
          { label: 'Mentions légales', href: '#' },
          { label: 'CGU', href: '#' },
          { label: 'Confidentialité', href: '#' }
        ]
      },
      {
        title: 'Contact',
        links: [{ label: 'contact@equa.io', href: 'mailto:contact@equa.io' }]
      }
    ];
      newsletterEmail = '';

  currentYear = new Date().getFullYear();
  onNewsletterSubmit(e?: Event): void {
    e?.preventDefault();
    if (this.newsletterEmail) {
      console.log('Newsletter signup:', this.newsletterEmail);
      this.newsletterEmail = '';
    }
  }
}
/*in demo add 
    <app-splash-cursor></app-splash-cursor>
    next to router-outlet    

    COPY PASTE
*/
