import { Component, OnDestroy, ElementRef, ViewChild, AfterViewInit, HostListener, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ThreeService } from '../../services/three.service';
import { ChartModule } from 'primeng/chart';
import { MarketsTableComponent } from '../../components/markets-table/markets-table.component';
import { HomeHeaderComponent } from '../../components/organisms/home-header/home-header.component';
import { StatsStripComponent } from '../../components/organisms/stats-strip/stats-strip.component';
import { PriceOverviewComponent } from '../../components/organisms/price-overview/price-overview.component';
import { FaqSectionComponent } from '../../components/organisms/faq-section/faq-section.component';
import { SiteFooterComponent } from '../../components/organisms/site-footer/site-footer.component';
import { TokenHeroComponent } from '../../components/organisms/token-hero/token-hero.component';
import { TokenIntroComponent } from '../../components/organisms/token-intro/token-intro.component';
import { TokensMarketComponent } from '../../components/organisms/tokens-market/tokens-market.component';
import { HowItWorksComponent, HowStepItem } from '../../components/organisms/how-it-works/how-it-works.component';
import { CtaBannerComponent } from '../../components/organisms/cta-banner/cta-banner.component';
import { LogoLoopBandComponent } from '../../components/organisms/logo-loop-band/logo-loop-band.component';
import { BackToTopButtonComponent } from '../../components/atoms/back-to-top-button/back-to-top-button.component';
import { NavMenuItem } from '../../components/molecules/nav-menu/nav-menu.component';
import { FaqSectionItem } from '../../components/organisms/faq-section/faq-section.component';
import { FooterLinkGroup } from '../../components/organisms/site-footer/site-footer.component';
import { PriceStatItem } from '../../components/organisms/price-overview/price-overview.component';
import { FinancialChartComponent } from '../../components/financial-chart/financial-chart.component';
import { getAcmeData, getAcmeAnnotations } from '../../components/financial-chart/financial-chart.data';
import { ChartLinesStyleComponent } from '../../components/chart-lines-style/chart-lines-style.component';

interface StatItem {
  label: string;
  target: number;
  suffix: string;
  current: number;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    MarketsTableComponent,
    HomeHeaderComponent,
    StatsStripComponent,
    PriceOverviewComponent,
    FaqSectionComponent,
    SiteFooterComponent,
    TokenHeroComponent,
    TokenIntroComponent,
    TokensMarketComponent,
    HowItWorksComponent,
    CtaBannerComponent,
    LogoLoopBandComponent,
    BackToTopButtonComponent,
    ChartModule,
    FinancialChartComponent,
    ChartLinesStyleComponent
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements AfterViewInit, OnDestroy {
  @ViewChild('statsSection') statsSectionRef!: ElementRef<HTMLElement>;

  loading = true;
  currentYear = new Date().getFullYear();
  glitchColors = ['#0f0c29', '#1a1a2e', '#FFD700', '#00d4ff', '#16213e'];
  tokenIntroDescription =
    "EQUA est un token conçu pour rendre la finance décentralisée accessible à tous. Transparent, sécurisé et simple d'usage, il s'intègre dans un écosystème qui vise à réduire les barrières d'accès aux services financiers.";
  ctaBannerText = "Rejoignez l'écosystème EQUA et accédez à la finance sans barrières.";

  showBackToTop = false;
  faqOpen: number | null = null;
  mobileMenuOpen = false;
  newsletterEmail = '';
  headerNavItems: NavMenuItem[] = [
    { label: 'Accueil', sectionId: 'token-intro' },
    { label: 'Token', sectionId: 'token-intro' },
    { label: 'Services', sectionId: 'how-it-works' },
    { label: 'FAQ', sectionId: 'faq' },
    { label: 'Contact', sectionId: 'contact-cta' }
  ];
  faqItems: FaqSectionItem[] = [
    {
      question: "Qu'est-ce qu'EQUA ?",
      answer:
        "EQUA est un token et un écosystème de services financiers décentralisés, conçu pour rendre la finance accessible à tous, partout."
    },
    {
      question: 'Comment acheter le token EQUA ?',
      answer:
        'Après création de compte, vous pouvez obtenir des EQUA via notre plateforme, par carte ou par virement, ou sur des échanges partenaires.'
    },
    {
      question: 'Est-ce sécurisé ?',
      answer:
        'Oui. Nous utilisons les standards de la blockchain et des bonnes pratiques pour protéger les fonds et les données des utilisateurs.'
    },
    {
      question: 'Où est disponible EQUA ?',
      answer:
        'EQUA est accessible dans de nombreux pays. Consultez la section Documentation pour la liste des juridictions supportées.'
    }
  ];
  priceOverview: {
    priceValue: string;
    priceChange: string;
    whyPriceLabel: string;
    stats: PriceStatItem[];
    tabs: string[];
    activeTab: string;
    chips: string[];
    activeChip: string;
  } = {
      priceValue: '1,00 €',
      priceChange: '0,0 % (24h)',
      whyPriceLabel: "Pourquoi le prix d'EQUA est-il en hausse ?",
      stats: [
        { label: 'Cap. Marché', value: '120,0 M €', tag: 'stable', tagVariant: 'default' },
        { label: 'Volume (24h)', value: '8,2 M €', tag: '+1,2 %', tagVariant: 'positive' },
        { label: 'Vol/Market Cap', value: '6,8 %' },
        { label: 'FDV', value: '120,0 M €' },
        { label: 'Offre totale', value: '120,0 M EQUA' },
        { label: 'Offre en circulation', value: '118,5 M EQUA' }
      ],
      tabs: ['Prix', 'Cap. Marché', 'TradingView'],
      activeTab: 'Prix',
      chips: ['24h', '1W', '1M', '1Y', 'Tout'],
      activeChip: '24h'
    };
  footerSocialLinks = [
    { label: 'Twitter', href: '#', ariaLabel: 'Twitter' },
    { label: 'LinkedIn', href: '#', ariaLabel: 'LinkedIn' },
    { label: 'Discord', href: '#', ariaLabel: 'Discord' },
    { label: 'Telegram', href: '#', ariaLabel: 'Telegram' }
  ];
  footerLinkGroups: FooterLinkGroup[] = [
    {
      title: 'Produit',
      links: [
        { label: 'Token', href: '#' },
        { label: 'Services', href: '#' },
        { label: 'Documentation', href: '#' }
      ]
    },
    {
      title: 'Ressources',
      links: [
        { label: 'Blog', href: '#' },
        { label: 'FAQ', href: '#' },
        { label: 'Support', href: '#' }
      ]
    },
    {
      title: 'Légal',
      links: [
        { label: 'Mentions légales', href: '#' },
        { label: 'CGU', href: '#' },
        { label: 'Confidentialité', href: '#' }
      ]
    },
    {
      title: 'Contact',
      links: [{ label: 'contact@equa.io', href: 'mailto:contact@equa.io' }]
    }
  ];
  howItWorksSteps: HowStepItem[] = [
    { number: '1', title: 'Créez un compte', text: 'Inscription simple et rapide avec votre email ou portefeuille.' },
    { number: '2', title: 'Obtenez des EQUA', text: 'Achetez ou recevez des tokens EQUA sur notre plateforme.' },
    { number: '3', title: 'Utilisez les services', text: 'Paiements, transferts et services décentralisés en un clic.' },
    { number: '4', title: 'Restez en contrôle', text: 'Vos actifs, votre portefeuille, une totale transparence.' }
  ];

  chartData = getAcmeData();
  chartTitle = 'Prix EQUA';
  chartAnnotations = getAcmeAnnotations();

  lineChartData = {
    labels: ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Juin', 'Juil'],
    datasets: [
      {
        label: 'Prix EQUA',
        data: [65, 59, 80, 81, 56, 55, 70],
        fill: false,
        tension: 0.4,
        borderColor: '#00bcd4',
        backgroundColor: 'rgba(0, 188, 212, 0.2)'
      }
    ]
  };

  lineChartOptions = {
    maintainAspectRatio: false,
    plugins: {
      legend: {
        labels: {
          color: '#ffffff'
        }
      }
    },
    scales: {
      x: {
        ticks: { color: '#cbd5e1' },
        grid: { color: 'rgba(255,255,255,0.12)' }
      },
      y: {
        ticks: { color: '#cbd5e1' },
        grid: { color: 'rgba(255,255,255,0.12)' }
      }
    }
  };

  stats: StatItem[] = [
    { label: 'Pays', target: 50, suffix: '+', current: 0 },
    { label: 'Utilisateurs', target: 10, suffix: 'K+', current: 0 },
    { label: 'Transactions', target: 1, suffix: 'M+', current: 0 },
    { label: 'Support', target: 24, suffix: '/7', current: 0 }
  ];
  private statsAnimated = false;
  private statsAnimationFrameId: number | null = null;
  private intersectionObserver: IntersectionObserver | null = null;
  private tokenCanvasRef: ElementRef<HTMLCanvasElement> | null = null;

  constructor(
    private threeService: ThreeService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) { }

  ngAfterViewInit(): void {
    const tokenImagePath = '/assets/images/equa-token-ref.png';
    if (this.tokenCanvasRef) {
      this.threeService.init(this.tokenCanvasRef, tokenImagePath);
      this.threeService.animate();
    }
    setTimeout(() => {
      this.loading = false;
    }, 1500);
    this.setupStatsObserver();
  }

  getStatDisplayText(stat: StatItem): string {
    return Math.round(stat.current) + stat.suffix;
  }

  get statsStripItems(): { value: string; label: string }[] {
    return this.stats.map((stat) => ({
      value: this.getStatDisplayText(stat),
      label: stat.label
    }));
  }

  private setupStatsObserver(): void {
    if (!this.statsSectionRef?.nativeElement) return;
    this.intersectionObserver = new IntersectionObserver(
      (entries) => {
        const entry = entries[0];
        if (entry?.isIntersecting && !this.statsAnimated) {
          this.statsAnimated = true;
          this.runStatsCountUp();
        }
      },
      { threshold: 0.3, rootMargin: '0px' }
    );
    this.intersectionObserver.observe(this.statsSectionRef.nativeElement);
  }

  private easeOutCubic(t: number): number {
    return 1 - Math.pow(1 - t, 3);
  }

  private runStatsCountUp(): void {
    const duration = 1800;
    const start = performance.now();

    const tick = (now: number) => {
      const elapsed = now - start;
      const progress = Math.min(elapsed / duration, 1);
      const eased = this.easeOutCubic(progress);

      this.stats.forEach((stat) => {
        stat.current = stat.target * eased;
      });
      this.cdr.detectChanges();

      if (progress < 1) {
        this.statsAnimationFrameId = requestAnimationFrame(tick);
      } else {
        this.stats.forEach((s) => (s.current = s.target));
        this.cdr.detectChanges();
      }
    };

    this.statsAnimationFrameId = requestAnimationFrame(tick);
  }

  ngOnDestroy(): void {
    this.threeService.dispose();
    if (this.statsAnimationFrameId != null) {
      cancelAnimationFrame(this.statsAnimationFrameId);
    }
    this.intersectionObserver?.disconnect();
  }

  @HostListener('window:scroll')
  onWindowScroll(): void {
    this.showBackToTop = window.scrollY > 400;
  }

  scrollTo(id: string): void {
    this.mobileMenuOpen = false;
    const el = document.getElementById(id);
    el?.scrollIntoView({ behavior: 'smooth' });
  }

  scrollToTop(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  onTokenCanvasReady(canvasRef: ElementRef<HTMLCanvasElement>): void {
    this.tokenCanvasRef = canvasRef;
  }

  toggleFaq(index: number): void {
    this.faqOpen = this.faqOpen === index ? null : index;
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  onHeaderCtaClick(event: Event): void {
    event.preventDefault();
  }

  onTokenPrimaryCtaClick(event: Event): void {
    event.preventDefault();
    this.router.navigate(['/blockchain']);
  }

  onTokenSecondaryCtaClick(event: Event): void {
    event.preventDefault();
    this.scrollTo('how-it-works');
  }

  onWhyPriceClick(): void { }

  onPriceTabSelect(tab: string): void {
    this.priceOverview = { ...this.priceOverview, activeTab: tab };
  }

  onPriceChipSelect(chip: string): void {
    this.priceOverview = { ...this.priceOverview, activeChip: chip };
  }

  onJoinCtaClick(event: Event): void {
    event.preventDefault();
  }

  onNewsletterSubmit(e?: Event): void {
    e?.preventDefault();
    if (this.newsletterEmail) {
      console.log('Newsletter signup:', this.newsletterEmail);
      this.newsletterEmail = '';
    }
  }
}
