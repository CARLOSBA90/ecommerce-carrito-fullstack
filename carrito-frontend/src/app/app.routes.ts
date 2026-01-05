import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'home',
    loadChildren: () => import('./features/home/home.routes').then(m => m.HOME_ROUTES)
  },
  {
    path: 'backoffice',
    loadChildren: () => import('./features/backoffice/backoffice.routes').then(m => m.BACKOFFICE_ROUTES)
  },
  { path: '**', redirectTo: 'home' }
];
