import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'blockchain',
    loadChildren: () => import('./BlockChain/blockchain.routes').then(m => m.BLOCKCHAIN_ROUTES)
  },
  {
    path: 'financial-market',
    loadChildren: () => import('./FiancialMarket/financial-market.routes').then(m => m.FINANCIAL_MARKET_ROUTES)
  },
  {
    path: 'forum',
    loadChildren: () => import('./Forum/forum.routes').then(m => m.FORUM_ROUTES)
  },
  {
    path: 'loan',
    loadChildren: () => import('./Loan/loan.routes').then(m => m.LOAN_ROUTES)
  },
  {
    path: 'user',
    loadChildren: () => import('./User/user.routes').then(m => m.USER_ROUTES)
  },
  {
    path: 'wallet',
    loadChildren: () => import('./Wallet/wallet.routes').then(m => m.WALLET_ROUTES)
  },
  {
    path: '**',
    redirectTo: ''
  }
];