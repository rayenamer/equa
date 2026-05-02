import { Routes } from '@angular/router';
import { HomepageComponent } from './homepage/homepage.component';
import { TokenDetailsComponent } from './token-details/token-details.component';

export const WALLET_ROUTES: Routes = [
  { path: '', component: HomepageComponent },
  { path: 'token', component: TokenDetailsComponent }
];
