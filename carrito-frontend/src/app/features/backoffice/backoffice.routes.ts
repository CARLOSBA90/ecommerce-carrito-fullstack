import { Routes } from '@angular/router';
import { BackofficeLayoutComponent } from './layout/backoffice-layout/backoffice-layout.component';

export const BACKOFFICE_ROUTES: Routes = [
    {
        path: '',
        component: BackofficeLayoutComponent,
        children: [
            {
                path: 'clientes/consulta',
                loadComponent: () => import('./components/customer-query/customer-query').then(m => m.CustomerQueryComponent)
            },
            {
                path: '',
                redirectTo: 'clientes/consulta',
                pathMatch: 'full'
            }
        ]
    }
];
