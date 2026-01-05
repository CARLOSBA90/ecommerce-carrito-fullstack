import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-backoffice-layout',
    standalone: true,
    imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
    templateUrl: './backoffice-layout.component.html',
    styleUrl: './backoffice-layout.component.css'
})
export class BackofficeLayoutComponent {

}
