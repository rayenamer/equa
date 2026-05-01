import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output, ChangeDetectorRef, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { NavMenuComponent, NavMenuItem } from '../../molecules/nav-menu/nav-menu.component';
import { UiButtonComponent } from '../../atoms/ui-button/ui-button.component';
import { BusinessModeService } from '../../../services/business-mode.service';

@Component({
  selector: 'app-home-header',
  standalone: true,
  imports: [CommonModule, RouterLink, NavMenuComponent, UiButtonComponent],
  templateUrl: './home-header.component.html',
  styleUrl: './home-header.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HomeHeaderComponent implements OnInit, OnDestroy {
  /** Brand text shown in the logo area. */
  @Input() logoText = 'EQUA';
  /** Navigation links rendered in the menu. */
  @Input() navItems: NavMenuItem[] = [];
  /** Mobile menu open/close state. */
  @Input() mobileMenuOpen = false;
  /** Login button text. */
  @Input() loginLabel = 'Connexion';
  /** Signup button text. */
  @Input() signupLabel = "S'inscrire";

  /** Emits when the burger menu is toggled. */
  @Output() menuToggle = new EventEmitter<void>();
  /** Emits selected section id from nav item click. */
  @Output() navItemClick = new EventEmitter<string>();
  /** Emits when login CTA is clicked. */
  @Output() loginClick = new EventEmitter<Event>();
  /** Emits when signup CTA is clicked. */
  @Output() signupClick = new EventEmitter<Event>();

  isBusinessMode = false;
  private readonly destroy$ = new Subject<void>();

  constructor(
    private businessModeService: BusinessModeService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.businessModeService.mode$
      .pipe(takeUntil(this.destroy$))
      .subscribe(mode => {
        this.isBusinessMode = mode === 'business';
        this.cdr.markForCheck();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
