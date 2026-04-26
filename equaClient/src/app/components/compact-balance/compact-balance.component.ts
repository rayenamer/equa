import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-compact-balance',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="compact-balance-card">
      <div class="icon-container" [style.background]="gradient">
        <span class="symbol">{{ symbol }}</span>
      </div>
      <div class="info">
        <span class="label">{{ label }}</span>
        <div class="amount-group">
          <span class="value">{{ amount | number:'1.2-2' }}</span>
          <span class="currency">{{ currency }}</span>
        </div>
      </div>
      <div class="noise-overlay"></div>
    </div>
  `,
    styles: [`
    .compact-balance-card {
      position: relative;
      background: rgba(255, 255, 255, 0.03);
      border: 1px solid rgba(255, 255, 255, 0.08);
      border-radius: 16px;
      padding: 1.25rem;
      display: flex;
      align-items: center;
      gap: 1.25rem;
      overflow: hidden;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      
      &:hover {
        background: rgba(255, 255, 255, 0.05);
        transform: translateY(-2px);
        border-color: rgba(255, 255, 255, 0.15);
        box-shadow: 0 10px 30px -10px rgba(0, 0, 0, 0.5);
      }
    }

    .icon-container {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.5rem;
      box-shadow: inset 0 0 10px rgba(255, 255, 255, 0.2);
    }

    .symbol {
      filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
    }

    .info {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .label {
      font-size: 0.75rem;
      font-weight: 600;
      color: rgba(255, 255, 255, 0.4);
      text-transform: uppercase;
      letter-spacing: 1px;
    }

    .amount-group {
      display: flex;
      align-items: baseline;
      gap: 0.4rem;
    }

    .value {
      font-size: 1.5rem;
      font-weight: 700;
      color: #fff;
      font-variant-numeric: tabular-nums;
    }

    .currency {
      font-size: 0.85rem;
      font-weight: 600;
      color: rgba(255, 255, 255, 0.6);
    }

    .noise-overlay {
      position: absolute;
      top: 0; left: 0; right: 0; bottom: 0;
      background-image: url('https://grainy-gradients.vercel.app/noise.svg');
      opacity: 0.05;
      pointer-events: none;
      mix-blend-mode: overlay;
    }
  `]
})
export class CompactBalanceComponent {
    @Input() label: string = '';
    @Input() amount: number = 0;
    @Input() currency: string = '';
    @Input() symbol: string = '';
    @Input() gradient: string = 'linear-gradient(135deg, #627eea 0%, #3d5afe 100%)';
}
