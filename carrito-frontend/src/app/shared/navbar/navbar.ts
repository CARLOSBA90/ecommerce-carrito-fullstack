import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  isLoggedIn = false;
  userName = 'Invitado';

  openCart(): void {
    console.log('Abrir popup de carrito');
  }

  openLogin(): void {
    console.log('Abrir popup de login');
  }

  openAccount(): void {
    console.log('Abrir popup de cuenta');
  }

}
