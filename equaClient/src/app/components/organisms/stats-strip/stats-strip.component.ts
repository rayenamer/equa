import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatCardComponent } from '../../molecules/stat-card/stat-card.component';

export interface StatsStripItem {
  value: string;
  label: string;
}

@Component({
  selector: 'app-stats-strip',
  standalone: true,
  imports: [CommonModule, StatCardComponent],
  templateUrl: './stats-strip.component.html',
  styleUrl: './stats-strip.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class StatsStripComponent {
  /** List of stats rendered in the strip. */
  @Input() stats: StatsStripItem[] = [];
}
