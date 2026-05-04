import { ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, Output, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { AuthService } from '../../../User/services/auth.service';
import { BusinessModeService } from '../../../services/business-mode.service';

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
  isBusinessMode = false;
  private readonly destroy$ = new Subject<void>();

  private readonly defaultItems: NavMenuItem[] = [
    { label: 'Features', sectionId: 'token-intro', route: '/landingPage' },
    { label: 'Docs', sectionId: 'token-intro' }
  ];

  private readonly authenticatedItems: NavMenuItem[] = [
    { label: 'Send / Receive', route: '/blockchain' },
    { label: 'Invest', route: '/financial-market' },
    { label: 'Community', route: '/forum' },
    { label: 'Wallet Overview', route: 'coming-soon' },
    { label: 'Borrow', route: '/coming-soon' }
  ];

  private get authenticatedActionItems(): NavMenuItem[] {
    return []; // We handle these separately in the dropdown now
  }

  isUserMenuOpen = false;

  toggleUserMenu(): void {
    this.isUserMenuOpen = !this.isUserMenuOpen;
    this.cdr.markForCheck();
  }

  get userMenuOptions() {
    if (this.isBusinessMode) {
      return [
        { label: 'Individual Mode', action: () => { this.businessModeService.setMode('individual'); this.router.navigate(['/user/dashboard']); this.isUserMenuOpen = false; } },
        { label: 'Profile', action: () => { this.router.navigate(['/user']); this.isUserMenuOpen = false; } },
        { label: 'Logout', action: () => { this.logout(); this.isUserMenuOpen = false; } }
      ];
    } else {
      return [
        { label: 'Business Mode', action: () => { this.businessModeService.setMode('business'); this.router.navigate(['/business/mouvements']); this.isUserMenuOpen = false; } },
        { label: 'Profile', action: () => { this.router.navigate(['/user']); this.isUserMenuOpen = false; } },
        { label: 'Logout', action: () => { this.logout(); this.isUserMenuOpen = false; } }
      ];
    }
  }

  private readonly anonymousActionItems: NavMenuItem[] = [
    { label: 'Login', route: '/user/login', styleType: 'link' },
  ];

  private readonly authenticatedBusinessItems: NavMenuItem[] = [];


  constructor(private authService: AuthService, private router: Router, private cdr: ChangeDetectorRef, private businessModeService: BusinessModeService) {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.authenticated = !!user;
        this.cdr.markForCheck();
      });

    this.businessModeService.mode$
      .pipe(takeUntil(this.destroy$))
      .subscribe(mode => {
        this.isBusinessMode = mode === 'business';
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
    const baseItems = this.isBusinessMode ? this.authenticatedBusinessItems : this.authenticatedItems;
    return this.authenticated
      ? [...baseItems, ...this.authenticatedActionItems]
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
