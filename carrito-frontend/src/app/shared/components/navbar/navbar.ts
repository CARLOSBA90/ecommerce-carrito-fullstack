import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CartService } from '../../../features/cart/services/cart.service';

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
  cartCount$;

  constructor(private cartService: CartService) {
    this.cartCount$ = this.cartService.count$;
  }

  openCart(): void {
    this.cartService.toggleCart();
  }

  openLogin(): void {
    console.log('Abrir popup de login');
  }

  openAccount(): void {
    console.log('Abrir popup de cuenta');
  }

}
