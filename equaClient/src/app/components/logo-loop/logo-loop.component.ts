import {
  Component,
  Input,
  ElementRef,
  ViewChild,
  AfterViewInit,
  OnDestroy,
  ChangeDetectorRef
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { LogoItem } from '../../models/logo-item.model';

const ANIMATION_CONFIG = { SMOOTH_TAU: 0.25, MIN_COPIES: 2, COPY_HEADROOM: 2 };

@Component({
  selector: 'app-logo-loop',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './logo-loop.component.html',
  styleUrls: ['./logo-loop.component.scss']
})
export class LogoLoopComponent implements AfterViewInit, OnDestroy {
  @ViewChild('container') containerRef!: ElementRef<HTMLDivElement>;
  @ViewChild('track') trackRef!: ElementRef<HTMLDivElement>;
  @ViewChild('firstList') firstListRef!: ElementRef<HTMLUListElement>;

  @Input() logos: LogoItem[] = [];
  @Input() speed = 120;
  @Input() direction: 'left' | 'right' | 'up' | 'down' = 'left';
  @Input() width = '100%';
  @Input() logoHeight = 28;
  @Input() gap = 32;
  @Input() pauseOnHover = true;
  @Input() hoverSpeed?: number;
  @Input() fadeOut = true;
  @Input() fadeOutColor = '';
  @Input() scaleOnHover = false;
  @Input() ariaLabel = 'Partenaires et tokens';

  seqWidth = 0;
  seqHeight = 0;
  copyCount = ANIMATION_CONFIG.MIN_COPIES;
  isHovered = false;

  private rafId: number | null = null;
  private lastTimestamp = 0;
  private offset = 0;
  private velocity = 0;
  private resizeObserver: ResizeObserver | null = null;
  private targetVelocity = 0;

  readonly defaultLogos: LogoItem[] = [
    { src: '/assets/images/equa-token-ref.png', alt: 'EQUA', title: 'EQUA' },
    { src: 'https://assets.coingecko.com/coins/images/1/small/bitcoin.png', alt: 'Bitcoin', title: 'BTC' },
    { src: 'https://assets.coingecko.com/coins/images/279/small/ethereum.png', alt: 'Ethereum', title: 'ETH' },
    { src: 'https://assets.coingecko.com/coins/images/825/small/bnb-icon2_2x.png', alt: 'BNB', title: 'BNB' },
    { src: 'https://assets.coingecko.com/coins/images/4128/small/solana.png', alt: 'Solana', title: 'SOL' },
    { src: 'https://assets.coingecko.com/coins/images/325/small/Tether.png', alt: 'Tether', title: 'USDT' },
    { src: 'https://assets.coingecko.com/coins/images/6319/small/usdc.png', alt: 'USD Coin', title: 'USDC' }
  ];

  get items(): LogoItem[] {
    return this.logos?.length ? this.logos : this.defaultLogos;
  }

  get isVertical(): boolean {
    return this.direction === 'up' || this.direction === 'down';
  }

  get effectiveHoverSpeed(): number | undefined {
    if (this.hoverSpeed !== undefined) return this.hoverSpeed;
    if (this.pauseOnHover) return 0;
    return undefined;
  }

  get rootClass(): string {
    const parts = [
      'logoloop',
      this.isVertical ? 'logoloop--vertical' : 'logoloop--horizontal',
      this.fadeOut ? 'logoloop--fade' : '',
      this.scaleOnHover ? 'logoloop--scale-hover' : ''
    ];
    return parts.filter(Boolean).join(' ');
  }

  get containerStyle(): Record<string, string> {
    const w = typeof this.width === 'number' ? `${this.width}px` : (this.width ?? '100%');
    const style: Record<string, string> = {
      '--logoloop-gap': `${this.gap}px`,
      '--logoloop-logoHeight': `${this.logoHeight}px`
    };
    if (this.fadeOutColor) style['--logoloop-fadeColor'] = this.fadeOutColor;
    if (this.isVertical) {
      if (w !== '100%') style['width'] = w;
    } else {
      style['width'] = w;
    }
    return style;
  }

  get copyArray(): number[] {
    return Array.from({ length: this.copyCount }, (_, i) => i);
  }

  constructor(private cdr: ChangeDetectorRef) {}

  ngAfterViewInit(): void {
    this.computeTargetVelocity();
    this.updateDimensions();
    this.setupResizeObserver();
    this.setupImageLoader();
    this.startAnimation();
  }

  ngOnDestroy(): void {
    this.resizeObserver?.disconnect();
    if (this.rafId !== null) cancelAnimationFrame(this.rafId);
  }

  private computeTargetVelocity(): void {
    const magnitude = Math.abs(this.speed);
    let mult = 1;
    if (this.isVertical) mult = this.direction === 'up' ? 1 : -1;
    else mult = this.direction === 'left' ? 1 : -1;
    if (this.speed < 0) mult *= -1;
    this.targetVelocity = magnitude * mult;
  }

  private updateDimensions(): void {
    const container = this.containerRef?.nativeElement;
    const firstList = this.firstListRef?.nativeElement;
    if (!container || !firstList) return;

    const rect = firstList.getBoundingClientRect();
    const seqW = Math.ceil(rect.width);
    const seqH = Math.ceil(rect.height);

    if (this.isVertical) {
      const parentHeight = container.parentElement?.clientHeight ?? 0;
      if (parentHeight > 0 && container.style.height !== `${parentHeight}px`) {
        container.style.height = `${parentHeight}px`;
      }
      if (seqH > 0) {
        this.seqHeight = seqH;
        const viewport = container.clientHeight || parentHeight || seqH;
        const copies = Math.ceil(viewport / seqH) + ANIMATION_CONFIG.COPY_HEADROOM;
        this.copyCount = Math.max(ANIMATION_CONFIG.MIN_COPIES, copies);
      }
    } else if (seqW > 0) {
      this.seqWidth = seqW;
      const containerWidth = container.clientWidth ?? 0;
      const copies = Math.ceil(containerWidth / seqW) + ANIMATION_CONFIG.COPY_HEADROOM;
      this.copyCount = Math.max(ANIMATION_CONFIG.MIN_COPIES, copies);
    }
    this.offset = this.offset % (this.isVertical ? this.seqHeight : this.seqWidth) || 0;
    this.cdr.markForCheck();
  }

  private setupResizeObserver(): void {
    const container = this.containerRef?.nativeElement;
    const firstList = this.firstListRef?.nativeElement;
    if (!window.ResizeObserver) {
      window.addEventListener('resize', () => this.updateDimensions());
      return;
    }
    const callback = () => this.updateDimensions();
    this.resizeObserver = new ResizeObserver(callback);
    if (container) this.resizeObserver.observe(container);
    if (firstList) this.resizeObserver.observe(firstList);
  }

  private setupImageLoader(): void {
    const firstList = this.firstListRef?.nativeElement;
    if (!firstList) {
      this.updateDimensions();
      return;
    }
    const images = firstList.querySelectorAll('img');
    if (images.length === 0) {
      this.updateDimensions();
      return;
    }
    let remaining = images.length;
    const onDone = () => {
      remaining--;
      if (remaining === 0) this.updateDimensions();
    };
    images.forEach((img) => {
      if ((img as HTMLImageElement).complete) onDone();
      else {
        img.addEventListener('load', onDone, { once: true });
        img.addEventListener('error', onDone, { once: true });
      }
    });
  }

  private startAnimation(): void {
    const track = this.trackRef?.nativeElement;
    if (!track) return;

    const animate = (timestamp: number) => {
      if (this.lastTimestamp === 0) this.lastTimestamp = timestamp;
      const deltaTime = Math.max(0, (timestamp - this.lastTimestamp) / 1000);
      this.lastTimestamp = timestamp;

      const seqSize = this.isVertical ? this.seqHeight : this.seqWidth;
      const target = this.effectiveHoverSpeed !== undefined && this.isHovered ? this.effectiveHoverSpeed : this.targetVelocity;
      const easing = 1 - Math.exp(-deltaTime / ANIMATION_CONFIG.SMOOTH_TAU);
      this.velocity += (target - this.velocity) * easing;

      if (seqSize > 0) {
        let next = this.offset + this.velocity * deltaTime;
        next = ((next % seqSize) + seqSize) % seqSize;
        this.offset = next;
        this.applyTransform(track, this.offset);
      }

      this.rafId = requestAnimationFrame(animate);
    };

    this.rafId = requestAnimationFrame(animate);
  }

  private applyTransform(track: HTMLDivElement, offset: number): void {
    if (this.isVertical) {
      track.style.transform = `translate3d(0, ${-offset}px, 0)`;
    } else {
      track.style.transform = `translate3d(${-offset}px, 0, 0)`;
    }
  }

  onMouseEnter(): void {
    if (this.effectiveHoverSpeed !== undefined) {
      this.isHovered = true;
      this.cdr.markForCheck();
    }
  }

  onMouseLeave(): void {
    if (this.effectiveHoverSpeed !== undefined) {
      this.isHovered = false;
      this.cdr.markForCheck();
    }
  }
}
