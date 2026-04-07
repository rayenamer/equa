import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ui-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ui-button.component.html',
  styleUrl: './ui-button.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UiButtonComponent {
  /** Visible button text. */
  @Input() label = '';
  /** Visual variant used for colors and border. */
  @Input() variant: 'primary' | 'secondary' = 'primary';
  /** Size variant used for spacing and font-size. */
  @Input() size: 'md' | 'lg' = 'md';
  /** Anchor destination; when provided, renders an anchor element. */
  @Input() href = '';
  /** Native button type when rendered as <button>. */
  @Input() type: 'button' | 'submit' = 'button';
  /** Disabled state for button/anchor interaction. */
  @Input() disabled = false;

  /** Emits when the control is activated by click. */
  @Output() clicked = new EventEmitter<Event>();

  onClick(event: Event): void {
    if (this.disabled) {
      event.preventDefault();
      event.stopPropagation();
      return;
    }
    this.clicked.emit(event);
  }
}
