import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  templateUrl: './stat-card.component.html',
  styleUrl: './stat-card.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class StatCardComponent {
  /** Main stat value shown prominently. */
  @Input() value = '';
  /** Descriptive label for the stat value. */
  @Input() label = '';
}
