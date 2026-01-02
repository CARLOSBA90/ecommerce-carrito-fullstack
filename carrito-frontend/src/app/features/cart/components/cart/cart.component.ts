import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart.service';
import { Observable } from 'rxjs';
import { CartProduct } from '../../interfaces/cart-product.interface';

@Component({
    selector: 'app-cart',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './cart.component.html',
    styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit {
    isOpen$: Observable<boolean>;
    items$: Observable<CartProduct[]>;
    total$: Observable<number>;

    constructor(private cartService: CartService) {
        this.isOpen$ = this.cartService.isOpen$;
        this.items$ = this.cartService.items$;
        this.total$ = this.cartService.total$;
    }

    ngOnInit(): void { }

    @HostListener('window:keydown.escape')
    onEsc(): void {
        this.closeCart();
    }

    closeCart(): void {
        this.cartService.closeCart();
    }

    updateQuantity(id: string, newQuantity: number): void {
        this.cartService.updateQuantity(id, newQuantity);
    }

    removeItem(id: string): void {
        this.cartService.removeFromCart(id);
    }

    checkout(): void {
        console.log('Proceed to checkout');
    }
}
