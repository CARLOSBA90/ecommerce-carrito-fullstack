import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CartComponent } from './features/cart/components/cart/cart.component';
import { ToastComponent } from './shared/components/toast/toast.component';
import { Navbar } from './shared/components/navbar/navbar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, CartComponent, ToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App { }
