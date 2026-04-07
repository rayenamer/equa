import { Routes } from '@angular/router';

export const FINANCIAL_MARKET_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./homepage/homepage.component').then(m => m.HomepageComponent)
  }
];