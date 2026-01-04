import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CartComponent } from './features/cart/components/cart/cart.component';
import { ToastComponent } from './shared/components/toast/toast.component';
import { Navbar } from './shared/components/navbar/navbar';
import { Login } from './features/auth/components/login/login';
import { CartService } from './features/cart/services/cart.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, CartComponent, ToastComponent, Login],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  constructor(private cartService: CartService) { }

  ngOnInit() {
    this.cartService.init();
  }
}
