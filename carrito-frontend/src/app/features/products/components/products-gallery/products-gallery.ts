import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Products, Product } from '../../../products/services/products';

@Component({
  selector: 'app-products-gallery',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './products-gallery.html',
  styleUrl: './products-gallery.css',
})
export class ProductsGallery implements OnInit {
  products: Product[] = [];

  constructor(private productsService: Products) {}

  ngOnInit(): void {
    this.productsService.getProducts().subscribe(data => {
      this.products = data;
    });
  }

  addToCart(product: Product): void {
    console.log('Producto agregado al carrito:', product);
    // TODO: Implementar lógica del carrito
  }
}
