import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { HomepageComponent as BlockchainHomepage } from './BlockChain/homepage/homepage.component';
import { HomepageComponent as FinancialMarketHomepage } from './FiancialMarket/homepage/homepage.component';
import { HomepageComponent as ForumHomepage } from './Forum/homepage/homepage.component';
import { HomepageComponent as LoanHomepage } from './Loan/homepage/homepage.component';
import { HomepageComponent as UserHomepage } from './User/homepage/homepage.component';
import { HomepageComponent as WalletHomepage } from './Wallet/homepage/homepage.component';
import { DashboardComponent } from './BlockChain/dashboard/dashboard.component';
import { ListComponent as TransactionListComponent } from './BlockChain/transactions/list/list.component';
import { DetailsComponent as TransactionDetailsComponent } from './BlockChain/transactions/details/details.component';
import { WalletComponent as BlockchainWalletComponent } from './BlockChain/wallet/wallet.component';
import { NodesComponent } from './BlockChain/nodes/nodes.component';
import { BlocksComponent } from './BlockChain/blocks/blocks.component';
import { AiInsightsComponent } from './BlockChain/ai-insights/ai-insights.component';
import { TransactionFormComponent } from './BlockChain/transaction-form/transaction-form.component';
import { ConsensusExplanationComponent } from './BlockChain/consensus-explanation/consensus-explanation.component';
import { ProfileComponent } from './User/profile/profile.component';
import { KycComponent } from './User/kyc/kyc.component';
import { AuditLogsComponent } from './User/audit-logs/audit-logs.component';
import { SecurityComponent } from './User/security/security.component';
import { LoginComponent } from './User/login/login.component';
import { RegisterComponent } from './User/register/register.component';
import { UsersDashboardComponent } from './User/users-dashboard/users-dashboard.component';
import { KycManagementComponent } from './User/kyc-management/kyc-management.component';
import { LandingPage } from './landing-page/landing-page';

export const routes: Routes = [
    {
        path: '',
        component: LandingPage
    },
    {
        path: 'home',
        component: HomeComponent
    },
    {
        path : 'landingPage',
        component: LandingPage
    },
    {
        path: 'blockchain',
        component: BlockchainHomepage,
        children: [
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
            { path: 'dashboard', component: DashboardComponent },
            {
                path: 'transactions',
                children: [
                    { path: '', component: TransactionListComponent },
                    { path: 'create', component: TransactionFormComponent },
                    { path: ':id', component: TransactionDetailsComponent }
                ]
            },
            { path: 'wallet', component: BlockchainWalletComponent },
            { path: 'nodes', component: NodesComponent },
            { path: 'blocks', component: BlocksComponent },
            { path: 'ai-insights', component: AiInsightsComponent },
            { path: 'explanation', component: ConsensusExplanationComponent }
        ]
    },
    {
        path: 'financial-market',
        component: FinancialMarketHomepage,
        children: [
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
            { path: 'dashboard', loadComponent: () => import('./FiancialMarket/market-dashboard/market-dashboard.component').then(m => m.MarketDashboardComponent) },
            { path: 'assets', loadComponent: () => import('./FiancialMarket/assets-list/assets-list.component').then(m => m.AssetsListComponent) },
            { path: 'assets/create', loadComponent: () => import('./FiancialMarket/asset-creation/asset-creation.component').then(m => m.AssetCreationComponent) },
            { path: 'assets/:id', loadComponent: () => import('./FiancialMarket/asset-trade/asset-trade.component').then(m => m.AssetTradeComponent) },
            { path: 'portfolio', loadComponent: () => import('./FiancialMarket/portfolio/portfolio.component').then(m => m.PortfolioComponent) },
            { path: 'explanation', loadComponent: () => import('./FiancialMarket/market-explanation/market-explanation.component').then(m => m.MarketExplanationComponent) }
        ]
    },
    {
        path: 'forum',
        component: ForumHomepage
    },
    {
        path: 'loan',
        component: LoanHomepage
    },
    {
        path: 'user',
        component: UserHomepage,
        children: [
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
            { path: 'dashboard', component: UsersDashboardComponent },
            { path: 'profile', component: ProfileComponent },
            { path: 'kyc', component: KycComponent },
            { path: 'kyc-management', component: KycManagementComponent },
            { path: 'audit-logs', component: AuditLogsComponent },
            { path: 'security', component: SecurityComponent },
            { path: 'login', component: LoginComponent },
            { path: 'register', component: RegisterComponent }
        ]
    },
    { path: 'forgot-password', loadComponent: () => import('./User/security/security.component').then(m => m.SecurityComponent) }, // Placeholder or separate component
    { path: 'coming-soon', loadComponent: () => import('./pages/coming-soon/coming-soon.component').then(m => m.ComingSoonComponent) },
    {
        path: 'wallet',
        component: WalletHomepage
    },
    {
        path: '**',
        redirectTo: ''
    }
];