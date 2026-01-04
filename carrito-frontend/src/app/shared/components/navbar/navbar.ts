import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CartService } from '../../../features/cart/services/cart.service';
import { AuthService } from '../../../features/auth/services/auth.service';
import { LoginModalService } from '../../../shared/services/login-modal.service';
import { ConfirmationModalComponent } from '../../components/confirmation-modal/confirmation-modal.component';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, ConfirmationModalComponent],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  isLoggedIn = false;
  userName = 'Invitado';
  cartCount$;

  // Logout Modal State
  showLogoutModal = false;

  constructor(
    private cartService: CartService,
    private authService: AuthService,
    private loginModalService: LoginModalService
  ) {
    this.cartCount$ = this.cartService.count$;

    // Subscribe to auth state
    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
      this.userName = user ? user.username : 'Invitado';
    });
  }

  openCart(): void {
    this.cartService.toggleCart();
  }

  openLogin(): void {
    this.loginModalService.openModal().subscribe();
  }

  openAccount(): void {
    // Show logout confirmation modal
    this.showLogoutModal = true;
  }

  onLogoutConfirm(): void {
    this.authService.logout();
    this.showLogoutModal = false;
  }

  onLogoutCancel(): void {
    this.showLogoutModal = false;
  }
}
