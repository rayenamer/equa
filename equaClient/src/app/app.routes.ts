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

export const routes: Routes = [
    {
        path: '',
        component: HomeComponent
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
            { path: 'ai-insights', component: AiInsightsComponent }
        ]
    },
    {
        path: 'financial-market',
        component: FinancialMarketHomepage
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
        component: UserHomepage
    },
    {
        path: 'wallet',
        component: WalletHomepage
    },
    {
        path: '**',
        redirectTo: ''
    }
];