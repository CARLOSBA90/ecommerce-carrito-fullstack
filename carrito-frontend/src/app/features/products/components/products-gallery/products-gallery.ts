import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductsService, Product } from '../../../products/services/products.service';
import { CartService } from '../../../cart/services/cart.service';

@Component({
  selector: 'app-products-gallery',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './products-gallery.html',
  styleUrl: './products-gallery.css',
})
export class ProductsGallery implements OnInit {
  products: Product[] = [];
  loading: boolean = true;
  error: string = '';

  constructor(
    private productsService: ProductsService,
    private cartService: CartService
  ) { }

  ngOnInit(): void {
    this.productsService.getProducts().subscribe({
      next: (data) => {
        this.products = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading products:', err);
        this.error = 'No se pudieron cargar los productos. Por favor, intenta de nuevo más tarde.';
        this.loading = false;
      }
    });
  }

  addToCart(product: Product): void {
    this.cartService.addToCart(product);
  }
}
