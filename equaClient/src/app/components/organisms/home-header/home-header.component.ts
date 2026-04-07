import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavMenuComponent, NavMenuItem } from '../../molecules/nav-menu/nav-menu.component';
import { UiButtonComponent } from '../../atoms/ui-button/ui-button.component';

@Component({
  selector: 'app-home-header',
  standalone: true,
  imports: [CommonModule, NavMenuComponent, UiButtonComponent],
  templateUrl: './home-header.component.html',
  styleUrl: './home-header.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HomeHeaderComponent {
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
}
