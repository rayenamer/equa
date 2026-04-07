import { Component, ElementRef, ViewChild, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

interface PricePoint {
  time: string;
  price: number;
}

interface HoverState {
  x: number; // viewBox coords (0-100)
  y: number; // viewBox coords (0-40)
  xPercent: number; // for CSS positioning
  yPercent: number;
  time: string;
  price: number;
}

@Component({
  selector: 'app-price-chart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './price-chart.component.html',
  styleUrls: ['./price-chart.component.scss'],
})
export class PriceChartComponent implements OnInit {
  @ViewChild('chartHost', { static: true }) chartHost!: ElementRef<HTMLDivElement>;

  points: PricePoint[] = [];
  minPrice = 0;
  maxPrice = 0;

  pathD = '';
  areaD = '';

  hover: HoverState | null = null;

  ngOnInit(): void {
    // Données mock pour 24h d'un stablecoin (~1.00 €)
    this.points = [
      { time: '12:00', price: 1.000 },
      { time: '14:00', price: 0.998 },
      { time: '16:00', price: 1.001 },
      { time: '18:00', price: 1.002 },
      { time: '20:00', price: 0.999 },
      { time: '22:00', price: 1.000 },
      { time: '00:00', price: 1.001 },
      { time: '02:00', price: 0.999 },
      { time: '04:00', price: 1.000 },
      { time: '06:00', price: 1.002 },
      { time: '08:00', price: 1.001 },
      { time: '10:00', price: 1.000 },
    ];

    this.minPrice = Math.min(...this.points.map((p) => p.price));
    this.maxPrice = Math.max(...this.points.map((p) => p.price));

    this.buildPaths();
  }

  private mapX(index: number): number {
    const n = this.points.length - 1;
    if (n === 0) return 0;
    return (index / n) * 100;
  }

  private mapY(price: number): number {
    const padding = 3; // espace haut/bas dans viewBox
    const min = this.minPrice;
    const max = this.maxPrice;
    if (max === min) return 20;
    const t = (price - min) / (max - min); // 0..1
    const yMin = padding;
    const yMax = 40 - padding;
    // Inverser (0 en haut, 40 en bas)
    return yMax - t * (yMax - yMin);
  }

  private buildPaths(): void {
    if (!this.points.length) {
      this.pathD = '';
      this.areaD = '';
      return;
    }

    let d = '';
    let area = '';

    this.points.forEach((pt, i) => {
      const x = this.mapX(i);
      const y = this.mapY(pt.price);
      if (i === 0) {
        d = `M ${x} ${y}`;
        area = `M ${x} 40 L ${x} ${y}`;
      } else {
        d += ` L ${x} ${y}`;
        area += ` L ${x} ${y}`;
      }
      if (i === this.points.length - 1) {
        area += ` L ${x} 40 Z`;
      }
    });

    this.pathD = d;
    this.areaD = area;
  }

  onMouseMove(event: MouseEvent): void {
    if (!this.points.length) return;
    const host = this.chartHost.nativeElement;
    const rect = host.getBoundingClientRect();
    const xPx = event.clientX - rect.left;
    const ratio = Math.min(1, Math.max(0, xPx / rect.width));
    const n = this.points.length - 1;
    const index = Math.round(ratio * n);
    const point = this.points[index];

    const xView = this.mapX(index);
    const yView = this.mapY(point.price);

    this.hover = {
      x: xView,
      y: yView,
      xPercent: ratio * 100,
      yPercent: ((yView / 40) * 100),
      time: point.time,
      price: point.price,
    };
  }

  onMouseLeave(): void {
    this.hover = null;
  }
}

