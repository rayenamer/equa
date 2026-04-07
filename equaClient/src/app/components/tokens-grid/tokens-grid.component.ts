import {
  Component,
  Input,
  ElementRef,
  ViewChild,
  AfterViewInit,
  OnDestroy
} from '@angular/core';
import { CommonModule } from '@angular/common';
import gsap from 'gsap';
import { TokenCard } from '../../models/token-card.model';

@Component({
  selector: 'app-tokens-grid',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tokens-grid.component.html',
  styleUrls: ['./tokens-grid.component.scss']
})
export class TokensGridComponent implements AfterViewInit, OnDestroy {
  @ViewChild('root') rootRef!: ElementRef<HTMLDivElement>;
  @ViewChild('overlay') overlayRef!: ElementRef<HTMLDivElement>;
  @ViewChild('fade') fadeRef!: ElementRef<HTMLDivElement>;

  @Input() items: TokenCard[] = [];
  @Input() radius = 300;
  @Input() columns = 3;
  @Input() rows = 2;
  @Input() damping = 0.45;
  @Input() fadeOut = 0.6;
  @Input() ease = 'power3.out';

  private setX: (v: number) => void = () => {};
  private setY: (v: number) => void = () => {};
  private pos = { x: 0, y: 0 };

  readonly defaultTokens: TokenCard[] = [
    {
      image: 'https://assets.coingecko.com/coins/images/1/small/bitcoin.png',
      title: 'Bitcoin',
      subtitle: 'BTC',
      description: 'Première cryptomonnaie décentralisée, créée en 2009. Réserve de valeur et moyen de paiement pair-à-pair sans intermédiaire.',
      borderColor: '#F7931A',
      gradient: 'linear-gradient(145deg, #F7931A, #000)'
    },
    {
      image: 'https://assets.coingecko.com/coins/images/279/small/ethereum.png',
      title: 'Ethereum',
      subtitle: 'ETH',
      description: 'Plateforme de contrats intelligents et d\'applications décentralisées (dApps). Pilier de la finance décentralisée (DeFi) et des NFT.',
      borderColor: '#627EEA',
      gradient: 'linear-gradient(210deg, #627EEA, #000)'
    },
    {
      image: 'https://assets.coingecko.com/coins/images/825/small/bnb-icon2_2x.png',
      title: 'BNB',
      subtitle: 'BNB',
      description: 'Token natif de la BNB Chain (ex-Binance Smart Chain). Utilisé pour les frais, le staking et les services sur l\'écosystème Binance.',
      borderColor: '#F3BA2F',
      gradient: 'linear-gradient(165deg, #F3BA2F, #000)'
    },
    {
      image: 'https://assets.coingecko.com/coins/images/4128/small/solana.png',
      title: 'Solana',
      subtitle: 'SOL',
      description: 'Blockchain haute performance pour applications décentralisées, conçue pour la scalabilité et des frais de transaction très bas.',
      borderColor: '#9945FF',
      gradient: 'linear-gradient(195deg, #9945FF, #000)'
    },
    {
      image: 'https://assets.coingecko.com/coins/images/325/small/Tether.png',
      title: 'Tether',
      subtitle: 'USDT',
      description: 'Stablecoin indexé sur le dollar américain. Le plus utilisé pour le trading et le transfert de valeur en crypto.',
      borderColor: '#26A17B',
      gradient: 'linear-gradient(225deg, #26A17B, #000)'
    },
    {
      image: 'https://assets.coingecko.com/coins/images/6319/small/usdc.png',
      title: 'USD Coin',
      subtitle: 'USDC',
      description: 'Stablecoin émis par Circle, adossé à des réserves en dollars. Très utilisé dans la DeFi et les paiements.',
      borderColor: '#2775CA',
      gradient: 'linear-gradient(135deg, #2775CA, #000)'
    }
  ];

  get data(): TokenCard[] {
    return this.items?.length ? this.items : this.defaultTokens;
  }

  ngAfterViewInit(): void {
    const el = this.rootRef?.nativeElement;
    if (!el) return;
    this.setX = gsap.quickSetter(el, '--x', 'px') as (v: number) => void;
    this.setY = gsap.quickSetter(el, '--y', 'px') as (v: number) => void;
    const rect = el.getBoundingClientRect();
    this.pos = { x: rect.width / 2, y: rect.height / 2 };
    this.setX(this.pos.x);
    this.setY(this.pos.y);
    const fadeEl = this.fadeRef?.nativeElement;
    const overlayEl = this.overlayRef?.nativeElement;
    if (fadeEl) gsap.set(fadeEl, { opacity: 0 });
    if (overlayEl) gsap.set(overlayEl, { opacity: 0 });
  }

  ngOnDestroy(): void {}

  private moveTo(x: number, y: number): void {
    gsap.to(this.pos, {
      x,
      y,
      duration: this.damping,
      ease: this.ease as gsap.EaseString,
      onUpdate: () => {
        this.setX(this.pos.x);
        this.setY(this.pos.y);
      },
      overwrite: true
    });
  }

  onGridPointerMove(e: PointerEvent): void {
    const el = this.rootRef?.nativeElement;
    if (!el) return;
    const r = el.getBoundingClientRect();
    this.moveTo(e.clientX - r.left, e.clientY - r.top);
    const fadeEl = this.fadeRef?.nativeElement;
    const overlayEl = this.overlayRef?.nativeElement;
    if (fadeEl) gsap.to(fadeEl, { opacity: 0, duration: 0.25, overwrite: true });
    if (overlayEl) gsap.to(overlayEl, { opacity: 1, duration: 0.25, overwrite: true });
  }

  onGridPointerLeave(): void {
    const fadeEl = this.fadeRef?.nativeElement;
    const overlayEl = this.overlayRef?.nativeElement;
    if (fadeEl) gsap.to(fadeEl, { opacity: 1, duration: this.fadeOut, overwrite: true });
    if (overlayEl) gsap.to(overlayEl, { opacity: 0, duration: this.fadeOut, overwrite: true });
  }

  onImgError(event: Event, card: TokenCard): void {
    const el = event.target as HTMLImageElement;
    if (el) {
      el.src = `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100' height='100' viewBox='0 0 100 100'%3E%3Crect fill='%23333' width='100' height='100'/%3E%3Ctext x='50' y='55' text-anchor='middle' fill='%23fff' font-size='28'%3E${card.subtitle.charAt(0)}%3C/text%3E%3C/svg%3E`;
    }
  }

  onCardMouseMove(e: MouseEvent, card: TokenCard): void {
    const cardEl = (e.currentTarget as HTMLElement);
    const rect = cardEl.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    cardEl.style.setProperty('--mouse-x', `${x}px`);
    cardEl.style.setProperty('--mouse-y', `${y}px`);
  }
}
