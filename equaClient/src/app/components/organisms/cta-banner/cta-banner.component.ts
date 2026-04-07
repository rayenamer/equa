import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UiButtonComponent } from '../../atoms/ui-button/ui-button.component';

@Component({
  selector: 'app-cta-banner',
  standalone: true,
  imports: [UiButtonComponent],
  templateUrl: './cta-banner.component.html',
  styleUrl: './cta-banner.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CtaBannerComponent {
  /** Section id used for in-page links. */
  @Input() sectionId = 'contact-cta';
  /** Headline text displayed in banner. */
  @Input() title = '';
  /** Supporting paragraph text. */
  @Input() text = '';
  /** CTA button text. */
  @Input() buttonLabel = '';

  /** Emits when CTA button is clicked. */
  @Output() buttonClick = new EventEmitter<Event>();
}
