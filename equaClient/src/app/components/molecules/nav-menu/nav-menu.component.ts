import { ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, Output, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { AuthService } from '../../../User/services/auth.service';

export interface NavMenuItem {
  label: string;
  sectionId?: string;
  route?: string;
  action?: () => void;
  icon?: string;
  styleType?: 'link' | 'button' | 'icon';
  type?: 'link' | 'header';
}

@Component({
  selector: 'app-nav-menu',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './nav-menu.component.html',
  styleUrl: './nav-menu.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavMenuComponent implements OnDestroy {
  /** Navigation items rendered in order. */
  @Input() items?: NavMenuItem[];
  /** Controls mobile expanded state. */
  @Input() open = false;

  /** Emits with the destination section id when an item is clicked. */
  @Output() itemClick = new EventEmitter<string>();

  authenticated = false;
  private readonly destroy$ = new Subject<void>();

  private readonly defaultItems: NavMenuItem[] = [
    { label: 'Accueil', sectionId: 'token-intro' },
    { label: 'Token', sectionId: 'token-intro' },
    { label: 'Services', sectionId: 'how-it-works' },
    { label: 'FAQ', sectionId: 'faq' },
    { label: 'Contact', sectionId: 'contact-cta' }
  ];

  private readonly authenticatedItems: NavMenuItem[] = [
    { label: 'Send / Receive', route: '/blockchain' },
    { label: 'Invest', route: '/financial-market' },
    { label: 'Borrow', route: '/loan' }
  ];

  private readonly authenticatedActionItems: NavMenuItem[] = [
    { label: 'Profile', route: '/user', icon: '👤', styleType: 'icon' },
    { label: 'Déconnexion', action: () => this.logout(), styleType: 'button', route: 'home' }
  ];

  private readonly anonymousActionItems: NavMenuItem[] = [
    { label: 'Connexion', route: '/user/login', styleType: 'link' },
  ];


  constructor(private authService: AuthService, private router: Router, private cdr: ChangeDetectorRef) {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.authenticated = !!user;
        this.cdr.markForCheck();
      });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/user/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get menuItems(): NavMenuItem[] {
    if (this.items && this.items.length > 0) {
      return this.items;
    }
    return this.authenticated
      ? [...this.authenticatedItems, ...this.authenticatedActionItems]
      : [...this.defaultItems, ...this.anonymousActionItems];
  }

  onItemClick(event: Event, sectionId?: string): void {
    event.preventDefault();
    if (!sectionId) {
      return;
    }
    this.itemClick.emit(sectionId);
  }
}
