import {
  Component,
  Input,
  HostListener,
  ChangeDetectorRef,
  OnInit,
  OnDestroy
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureCardItem } from '../../models/feature-card.model';

@Component({
  selector: 'app-feature-stack',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './feature-stack.component.html',
  styleUrls: ['./feature-stack.component.scss']
})
export class FeatureStackComponent implements OnInit, OnDestroy {
  @Input() cards: FeatureCardItem[] = [];
  @Input() sensitivity = 200;
  @Input() sendToBackOnClick = true;
  @Input() autoplay = false;
  @Input() autoplayDelay = 3000;
  @Input() pauseOnHover = false;
  @Input() animationStiffness = 260;
  @Input() animationDamping = 20;

  order: number[] = [];
  draggingId: number | null = null;
  dragOffsetX = 0;
  dragOffsetY = 0;
  isPaused = false;
  private startX = 0;
  private startY = 0;
  private autoplayTimer: ReturnType<typeof setInterval> | null = null;

  readonly defaultCards: FeatureCardItem[] = [
    { icon: '◆', title: 'Accessible', text: 'Conçu pour être utilisé par le plus grand nombre, sans prérequis techniques.' },
    { icon: '◇', title: 'Transparent', text: 'Fonctionnement et règles clairs, sur une blockchain vérifiable.' },
    { icon: '●', title: 'Sécurisé', text: 'Infrastructure robuste et bonnes pratiques pour protéger les utilisateurs.' },
    { icon: '★', title: 'Innovant', text: 'Au cœur d\'un écosystème en évolution : paiements, services et partenariats.' }
  ];

  get items(): FeatureCardItem[] {
    return this.cards?.length ? this.cards : this.defaultCards;
  }

  get stackLength(): number {
    return this.items.length;
  }

  constructor(private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.order = this.items.map((_, i) => i);
    if (this.autoplay) this.startAutoplay();
  }

  ngOnDestroy(): void {
    if (this.autoplayTimer) clearInterval(this.autoplayTimer);
  }

  private startAutoplay(): void {
    this.autoplayTimer = setInterval(() => {
      if (this.isPaused || this.stackLength <= 1) return;
      this.sendToBack(this.order[this.order.length - 1]);
    }, this.autoplayDelay);
  }

  getCardStyle(contentIndex: number): Record<string, string> {
    const pos = this.order.indexOf(contentIndex);
    const scale = 1 + pos * 0.04 - this.stackLength * 0.04;
    const rotateZ = (this.stackLength - pos - 1) * 2;
    const isDragging = this.draggingId === contentIndex;
    const translateZ = pos * 2;

    let style: Record<string, string> = {
      'z-index': String(pos),
      transform: `translateZ(${translateZ}px) rotateZ(${rotateZ}deg) scale(${scale})`
    };

    if (isDragging) {
      const rotateX = this.mapRange(this.dragOffsetY, -100, 100, 60, -60);
      const rotateY = this.mapRange(this.dragOffsetX, -100, 100, -60, 60);
      style['transform'] = `translate3d(${this.dragOffsetX}px, ${this.dragOffsetY}px, ${translateZ}px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) rotateZ(${rotateZ}deg) scale(${scale})`;
      style['cursor'] = 'grabbing';
    }

    return style;
  }

  private mapRange(value: number, inMin: number, inMax: number, outMin: number, outMax: number): number {
    return outMin + (value - inMin) * (outMax - outMin) / (inMax - inMin);
  }

  onPointerDown(e: PointerEvent, contentIndex: number): void {
    e.preventDefault();
    this.draggingId = contentIndex;
    this.startX = e.clientX;
    this.startY = e.clientY;
    this.dragOffsetX = 0;
    this.dragOffsetY = 0;
    (e.target as HTMLElement).setPointerCapture?.(e.pointerId);
    this.cdr.markForCheck();
  }

  onPointerMove(e: PointerEvent): void {
    if (this.draggingId === null) return;
    this.dragOffsetX = e.clientX - this.startX;
    this.dragOffsetY = e.clientY - this.startY;
    this.cdr.markForCheck();
  }

  onPointerUp(e: PointerEvent, contentIndex: number): void {
    (e.target as HTMLElement).releasePointerCapture?.(e.pointerId);
    if (this.draggingId !== contentIndex) return;

    const distance = Math.sqrt(this.dragOffsetX ** 2 + this.dragOffsetY ** 2);
    if (distance > this.sensitivity) {
      this.sendToBack(contentIndex);
    }

    this.draggingId = null;
    this.dragOffsetX = 0;
    this.dragOffsetY = 0;
    this.cdr.markForCheck();
  }

  onCardClick(contentIndex: number): void {
    if (!this.sendToBackOnClick) return;
    const pos = this.order.indexOf(contentIndex);
    if (pos === this.order.length - 1) {
      this.sendToBack(contentIndex);
    }
  }

  sendToBack(contentIndex: number): void {
    const idx = this.order.indexOf(contentIndex);
    if (idx < 0) return;
    const card = this.order[idx];
    const newOrder = [card, ...this.order.filter((_, i) => i !== idx)];
    this.order = newOrder;
    this.cdr.markForCheck();
  }

  onContainerMouseEnter(): void {
    if (this.pauseOnHover) this.isPaused = true;
  }

  onContainerMouseLeave(): void {
    if (this.pauseOnHover) this.isPaused = false;
  }

  @HostListener('document:pointermove', ['$event'])
  docPointerMove(e: PointerEvent): void {
    this.onPointerMove(e);
  }

  @HostListener('document:pointerup', ['$event'])
  docPointerUp(e: PointerEvent): void {
    if (this.draggingId !== null) {
      this.dragOffsetX = 0;
      this.dragOffsetY = 0;
      this.draggingId = null;
      this.cdr.markForCheck();
    }
  }
}
