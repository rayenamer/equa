import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { FeatureStackComponent } from '../../feature-stack/feature-stack.component';

@Component({
  selector: 'app-token-intro',
  standalone: true,
  imports: [FeatureStackComponent],
  templateUrl: './token-intro.component.html',
  styleUrl: './token-intro.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TokenIntroComponent {
  /** Section id used for anchor navigation. */
  @Input() sectionId = 'token-intro';
  /** Main section heading text. */
  @Input() title = '';
  /** Secondary tagline text. */
  @Input() tagline = '';
  /** Paragraph describing the token. */
  @Input() description = '';
}
