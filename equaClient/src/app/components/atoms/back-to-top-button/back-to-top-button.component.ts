import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-back-to-top-button',
  standalone: true,
  templateUrl: './back-to-top-button.component.html',
  styleUrl: './back-to-top-button.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BackToTopButtonComponent {
  /** Controls visibility based on scroll state. */
  @Input() visible = false;
  /** ARIA label announced to assistive technologies. */
  @Input() ariaLabel = 'Retour en haut';

  /** Emits when the button is clicked. */
  @Output() pressed = new EventEmitter<void>();
}
