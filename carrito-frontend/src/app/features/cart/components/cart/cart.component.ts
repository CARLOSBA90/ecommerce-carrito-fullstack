import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { CartProduct } from '../../interfaces/cart-product.interface';
import { AppliedDiscount } from '../../interfaces/cart.interface';
import { OrderSummaryModalComponent } from '../../components/order-summary-modal/order-summary-modal';
import { OrderResponse } from '../../interfaces/order-response.interface';

@Component({
    selector: 'app-cart',
    standalone: true,
    imports: [CommonModule, OrderSummaryModalComponent],
    templateUrl: './cart.component.html',
    styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit {
    isOpen$: Observable<boolean>;
    items$: Observable<CartProduct[]>;
    subtotal$: Observable<number>;
    totalDiscounts$: Observable<number>;
    totalAmount$: Observable<number>;
    appliedDiscounts$: Observable<AppliedDiscount[]>;

    // Modal State
    showOrderSummary = false;
    confirmedOrder: OrderResponse | null = null;

    constructor(private cartService: CartService) {
        this.isOpen$ = this.cartService.isOpen$;
        this.items$ = this.cartService.items$;
        this.subtotal$ = this.cartService.cart$.pipe(
            map(cart => cart?.subtotal || 0)
        );
        this.totalDiscounts$ = this.cartService.cart$.pipe(
            map(cart => cart?.totalDiscounts || 0)
        );
        this.totalAmount$ = this.cartService.cart$.pipe(
            map(cart => cart?.totalAmount !== undefined ? cart.totalAmount : (cart?.subtotal || 0))
        );
        this.appliedDiscounts$ = this.cartService.cart$.pipe(
            map(cart => cart?.appliedDiscounts || [])
        );
    }

    ngOnInit(): void { }

    @HostListener('window:keydown.escape')
    onEsc(): void {
        this.closeCart();
    }

    closeCart(): void {
        this.cartService.closeCart();
    }

    updateQuantity(productId: number, newQuantity: number): void {
        this.cartService.updateQuantity(productId, newQuantity);
    }

    removeItem(productId: number): void {
        this.cartService.removeFromCart(productId);
    }

    clearCart(): void {
        if (confirm('¿Estás seguro de vaciar tu carrito?')) {
            this.cartService.clearCart();
        }
    }

    checkout(): void {
        this.cartService.confirmCart().subscribe({
            next: (order: OrderResponse) => {
                this.confirmedOrder = order;
                this.showOrderSummary = true;
                this.closeCart(); // Close sidebar
            },
            error: (err: any) => {
                console.error('Checkout error:', err);
                // Optionally show toast error via service if not handled globally
            }
        });
    }

    trackByProductId(index: number, item: CartProduct): number {
        return item.product.id;
    }

    closeOrderSummary(): void {
        this.showOrderSummary = false;
        this.confirmedOrder = null;
    }
}
