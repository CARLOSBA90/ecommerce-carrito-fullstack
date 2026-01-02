import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Products, Product } from '../../../products/services/products.service';
import { CartService } from '../../../cart/services/cart.service';
import { CartProduct } from '../../../cart/interfaces/cart-product.interface';

@Component({
  selector: 'app-products-gallery',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './products-gallery.html',
  styleUrl: './products-gallery.css',
})
export class ProductsGallery implements OnInit {
  products: Product[] = [];

  constructor(
    private productsService: Products,
    private cartService: CartService
  ) { }

  ngOnInit(): void {
    this.productsService.getProducts().subscribe(data => {
      this.products = data;
    });
  }

  addToCart(product: Product): void {
    const cartProduct: Omit<CartProduct, 'quantity'> = {
      productId: product.id.toString(),
      name: product.name,
      price: product.price,
      image: product.image
    };
    this.cartService.addToCart(cartProduct);
  }
}
