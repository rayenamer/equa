import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { HomepageComponent as BlockchainHomepage } from './BlockChain/homepage/homepage.component';
import { HomepageComponent as FinancialMarketHomepage } from './FiancialMarket/homepage/homepage.component';
import { HomepageComponent as ForumHomepage } from './Forum/homepage/homepage.component';
import { HomepageComponent as LoanHomepage } from './Loan/homepage/homepage.component';
import { HomepageComponent as UserHomepage } from './User/homepage/homepage.component';
import { HomepageComponent as WalletHomepage } from './Wallet/homepage/homepage.component';

export const routes: Routes = [
{
path: '',
component: HomeComponent
},
{
path: 'blockchain',
component: BlockchainHomepage
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