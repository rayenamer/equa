import { Routes } from '@angular/router';

export const FORUM_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./homepage/homepage.component').then(m => m.HomepageComponent)
  }
];