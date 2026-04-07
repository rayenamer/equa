import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-how-step',
  standalone: true,
  templateUrl: './how-step.component.html',
  styleUrl: './how-step.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HowStepComponent {
  /** Ordinal step number displayed in the badge. */
  @Input() stepNumber = '';
  /** Step title text. */
  @Input() title = '';
  /** Supporting step description text. */
  @Input() text = '';
}
