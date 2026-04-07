import { AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LetterGlitchComponent } from '../../letter-glitch/letter-glitch.component';
import { UiButtonComponent } from '../../atoms/ui-button/ui-button.component';

@Component({
  selector: 'app-token-hero',
  standalone: true,
  imports: [CommonModule, LetterGlitchComponent, UiButtonComponent],
  templateUrl: './token-hero.component.html',
  styleUrl: './token-hero.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TokenHeroComponent implements AfterViewInit {
  @ViewChild('canvasRef', { static: true }) private canvasRef!: ElementRef<HTMLCanvasElement>;

  /** Section id used for in-page navigation. */
  @Input() sectionId = 'token-section';
  /** Colors fed into the glitch background component. */
  @Input() glitchColors: string[] = [];
  /** Supporting text shown in left panel. */
  @Input() description = '';
  /** Primary CTA label. */
  @Input() primaryCtaLabel = '';
  /** Secondary CTA label. */
  @Input() secondaryCtaLabel = '';

  /** Emits when primary CTA is clicked. */
  @Output() primaryCtaClick = new EventEmitter<Event>();
  /** Emits when secondary CTA is clicked. */
  @Output() secondaryCtaClick = new EventEmitter<Event>();
  /** Emits canvas element so parent can initialize rendering. */
  @Output() canvasReady = new EventEmitter<ElementRef<HTMLCanvasElement>>();

  ngAfterViewInit(): void {
    this.canvasReady.emit(this.canvasRef);
  }
}
