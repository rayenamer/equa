import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HowStepComponent } from '../../molecules/how-step/how-step.component';

export interface HowStepItem {
  number: string;
  title: string;
  text: string;
}

@Component({
  selector: 'app-how-it-works',
  standalone: true,
  imports: [CommonModule, HowStepComponent],
  templateUrl: './how-it-works.component.html',
  styleUrl: './how-it-works.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HowItWorksComponent {
  /** Section id for anchor navigation. */
  @Input() sectionId = 'how-it-works';
  /** Heading displayed above the steps list. */
  @Input() title = '';
  /** Ordered steps rendered in the grid. */
  @Input() steps: HowStepItem[] = [];
}
