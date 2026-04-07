import {
  Component,
  Input,
  ElementRef,
  ViewChild,
  AfterViewInit,
  OnDestroy
} from '@angular/core';
import { CommonModule } from '@angular/common';

interface LetterCell {
  char: string;
  color: string;
  targetColor: string;
  colorProgress: number;
}

@Component({
  selector: 'app-letter-glitch',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './letter-glitch.component.html',
  styleUrls: ['./letter-glitch.component.scss']
})
export class LetterGlitchComponent implements AfterViewInit, OnDestroy {
  @ViewChild('glitchCanvas') canvasRef!: ElementRef<HTMLCanvasElement>;

  @Input() glitchColors = ['#2b4539', '#61dca3', '#61b3dc'];
  @Input() glitchSpeed = 50;
  @Input() centerVignette = false;
  @Input() outerVignette = true;
  @Input() smooth = true;
  @Input() characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$&*()-_+=/[]{};:<>.,0123456789';

  private letters: LetterCell[] = [];
  private grid = { columns: 0, rows: 0 };
  private ctx: CanvasRenderingContext2D | null = null;
  private lastGlitchTime = 0;
  private animationId: number | null = null;
  private resizeTimeout: ReturnType<typeof setTimeout> | null = null;

  private readonly fontSize = 16;
  private readonly charWidth = 10;
  private readonly charHeight = 20;
  private lettersAndSymbols: string[] = [];

  ngAfterViewInit(): void {
    this.lettersAndSymbols = Array.from(this.characters);
    this.ctx = this.canvasRef.nativeElement.getContext('2d');
    this.resizeCanvas();
    this.animate();

    window.addEventListener('resize', this.handleResize);
  }

  ngOnDestroy(): void {
    if (this.animationId !== null) cancelAnimationFrame(this.animationId);
    window.removeEventListener('resize', this.handleResize);
    if (this.resizeTimeout) clearTimeout(this.resizeTimeout);
  }

  private handleResize = (): void => {
    if (this.resizeTimeout) clearTimeout(this.resizeTimeout);
    this.resizeTimeout = setTimeout(() => {
      if (this.animationId !== null) cancelAnimationFrame(this.animationId);
      this.resizeCanvas();
      this.animate();
      this.resizeTimeout = null;
    }, 100);
  };

  private getRandomChar(): string {
    return this.lettersAndSymbols[Math.floor(Math.random() * this.lettersAndSymbols.length)];
  }

  private getRandomColor(): string {
    return this.glitchColors[Math.floor(Math.random() * this.glitchColors.length)];
  }

  private parseColor(color: string): { r: number; g: number; b: number } | null {
    const rgbMatch = /rgb\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)/.exec(color);
    if (rgbMatch) {
      return { r: +rgbMatch[1], g: +rgbMatch[2], b: +rgbMatch[3] };
    }
    const hex = color.replace(/^#?([a-f\d])([a-f\d])([a-f\d])$/i, (_, r, g, b) => r + r + g + g + b + b);
    const hexMatch = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return hexMatch
      ? { r: parseInt(hexMatch[1], 16), g: parseInt(hexMatch[2], 16), b: parseInt(hexMatch[3], 16) }
      : null;
  }

  private interpolateColor(
    start: { r: number; g: number; b: number },
    end: { r: number; g: number; b: number },
    factor: number
  ): string {
    const r = Math.round(start.r + (end.r - start.r) * factor);
    const g = Math.round(start.g + (end.g - start.g) * factor);
    const b = Math.round(start.b + (end.b - start.b) * factor);
    return `rgb(${r}, ${g}, ${b})`;
  }

  private calculateGrid(width: number, height: number): { columns: number; rows: number } {
    return {
      columns: Math.ceil(width / this.charWidth),
      rows: Math.ceil(height / this.charHeight)
    };
  }

  private initializeLetters(columns: number, rows: number): void {
    this.grid = { columns, rows };
    const total = columns * rows;
    this.letters = Array.from({ length: total }, () => ({
      char: this.getRandomChar(),
      color: this.getRandomColor(),
      targetColor: this.getRandomColor(),
      colorProgress: 1
    }));
  }

  private resizeCanvas(): void {
    const canvas = this.canvasRef?.nativeElement;
    const parent = canvas?.parentElement;
    if (!canvas || !parent) return;

    const dpr = window.devicePixelRatio || 1;
    const rect = parent.getBoundingClientRect();

    canvas.width = rect.width * dpr;
    canvas.height = rect.height * dpr;
    canvas.style.width = `${rect.width}px`;
    canvas.style.height = `${rect.height}px`;

    if (this.ctx) {
      this.ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    }

    const { columns, rows } = this.calculateGrid(rect.width, rect.height);
    this.initializeLetters(columns, rows);
    this.drawLetters();
  }

  private drawLetters(): void {
    if (!this.ctx || this.letters.length === 0) return;
    const canvas = this.canvasRef.nativeElement;
    const rect = canvas.getBoundingClientRect();
    this.ctx.clearRect(0, 0, rect.width, rect.height);
    this.ctx.font = `${this.fontSize}px monospace`;
    this.ctx.textBaseline = 'top';

    this.letters.forEach((letter, index) => {
      const x = (index % this.grid.columns) * this.charWidth;
      const y = Math.floor(index / this.grid.columns) * this.charHeight;
      this.ctx!.fillStyle = letter.color;
      this.ctx!.fillText(letter.char, x, y);
    });
  }

  private updateLetters(): void {
    if (this.letters.length === 0) return;
    const updateCount = Math.max(1, Math.floor(this.letters.length * 0.05));

    for (let i = 0; i < updateCount; i++) {
      const index = Math.floor(Math.random() * this.letters.length);
      const letter = this.letters[index];
      if (!letter) continue;

      letter.char = this.getRandomChar();
      letter.targetColor = this.getRandomColor();
      if (!this.smooth) {
        letter.color = letter.targetColor;
        letter.colorProgress = 1;
      } else {
        letter.colorProgress = 0;
      }
    }
  }

  private handleSmoothTransitions(): void {
    let needsRedraw = false;
    this.letters.forEach((letter) => {
      if (letter.colorProgress < 1) {
        letter.colorProgress += 0.05;
        if (letter.colorProgress > 1) letter.colorProgress = 1;
        const startRgb = this.parseColor(letter.color);
        const endRgb = this.parseColor(letter.targetColor);
        if (startRgb && endRgb) {
          letter.color = this.interpolateColor(startRgb, endRgb, letter.colorProgress);
          needsRedraw = true;
        }
      }
    });
    if (needsRedraw) this.drawLetters();
  }

  private animate = (): void => {
    const now = Date.now();
    if (now - this.lastGlitchTime >= this.glitchSpeed) {
      this.updateLetters();
      this.drawLetters();
      this.lastGlitchTime = now;
    }
    if (this.smooth) this.handleSmoothTransitions();
    this.animationId = requestAnimationFrame(this.animate);
  };
}
