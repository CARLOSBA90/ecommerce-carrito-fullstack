import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Navbar } from '../../../../shared/components/navbar/navbar';
import { CartComponent } from '../../../cart/components/cart/cart.component';
import { Login } from '../../../auth/components/login/login';
import { CartService } from '../../../cart/services/cart.service';

@Component({
  selector: 'app-home-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, Navbar, CartComponent, Login],
  templateUrl: './home-layout.component.html',
  styleUrl: './home-layout.component.css'
})
export class HomeLayoutComponent implements OnInit {
  constructor(private cartService: CartService) { }

  ngOnInit() {
    this.cartService.init();
  }
}
