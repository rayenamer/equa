import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-faq-item',
  standalone: true,
  templateUrl: './faq-item.component.html',
  styleUrl: './faq-item.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FaqItemComponent {
  /** FAQ question text. */
  @Input() question = '';
  /** FAQ answer text. */
  @Input() answer = '';
  /** Expanded state of the item. */
  @Input() open = false;

  /** Emits when the item header is clicked. */
  @Output() toggled = new EventEmitter<void>();
}
