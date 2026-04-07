import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FaqItemComponent } from '../../molecules/faq-item/faq-item.component';

export interface FaqSectionItem {
  question: string;
  answer: string;
}

@Component({
  selector: 'app-faq-section',
  standalone: true,
  imports: [CommonModule, FaqItemComponent],
  templateUrl: './faq-section.component.html',
  styleUrl: './faq-section.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FaqSectionComponent {
  /** FAQ section heading text. */
  @Input() title = 'Questions frequentes';
  /** FAQ items rendered in the accordion. */
  @Input() items: FaqSectionItem[] = [];
  /** Index of the currently opened item. */
  @Input() openIndex: number | null = null;

  /** Emits the item index requested for toggle. */
  @Output() itemToggle = new EventEmitter<number>();
}
