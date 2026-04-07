import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PriceChartComponent } from '../../price-chart/price-chart.component';

export interface PriceStatItem {
  label: string;
  value: string;
  tag?: string;
  tagVariant?: 'default' | 'positive' | 'negative';
}

@Component({
  selector: 'app-price-overview',
  standalone: true,
  imports: [CommonModule, PriceChartComponent],
  templateUrl: './price-overview.component.html',
  styleUrl: './price-overview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PriceOverviewComponent {
  /** Current token price text. */
  @Input() priceValue = '';
  /** Price delta text for selected period. */
  @Input() priceChange = '';
  /** Text displayed on the info button. */
  @Input() whyPriceLabel = '';
  /** Left-side metric cards under the main price. */
  @Input() stats: PriceStatItem[] = [];
  /** Tabs shown above the chart area. */
  @Input() tabs: string[] = [];
  /** Active tab label. */
  @Input() activeTab = '';
  /** Time-range chips shown above the chart area. */
  @Input() chips: string[] = [];
  /** Active chip label. */
  @Input() activeChip = '';

  /** Emits when "why price" button is clicked. */
  @Output() whyPriceClick = new EventEmitter<void>();
  /** Emits selected tab label. */
  @Output() tabSelect = new EventEmitter<string>();
  /** Emits selected chip label. */
  @Output() chipSelect = new EventEmitter<string>();
}
