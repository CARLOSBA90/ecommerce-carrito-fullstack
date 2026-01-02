import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { CartProduct } from '../interfaces/cart-product.interface';
import { ToastService } from '../../../shared/services/toast.service';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private _isOpen = new BehaviorSubject<boolean>(false);
  private _items = new BehaviorSubject<CartProduct[]>([]);

  // Public Observables
  isOpen$ = this._isOpen.asObservable();
  items$ = this._items.asObservable();

  count$: Observable<number> = this.items$.pipe(
    map((items) => items.reduce((acc, item) => acc + item.quantity, 0))
  );

  total$: Observable<number> = this.items$.pipe(
    map((items) => items.reduce((acc, item) => acc + item.price * item.quantity, 0))
  );

  constructor(private toastService: ToastService) { }

  toggleCart(): void {
    this._isOpen.next(!this._isOpen.value);
  }

  openCart(): void {
    this._isOpen.next(true);
  }

  closeCart(): void {
    this._isOpen.next(false);
  }

  addToCart(product: Omit<CartProduct, 'quantity'>): void {
    const currentItems = this._items.value;
    const existingItem = currentItems.find((item) => item.productId === product.productId);

    if (existingItem) {
      this.updateQuantity(product.productId, existingItem.quantity + 1);
    } else {
      this._items.next([...currentItems, { ...product, quantity: 1 }]);
    }

    this.toastService.show('Producto agregado al carrito');
  }

  removeFromCart(productId: string): void {
    const currentItems = this._items.value;
    this._items.next(currentItems.filter((item) => item.productId !== productId));
  }

  updateQuantity(productId: string, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return;
    }

    const currentItems = this._items.value;
    const updatedItems = currentItems.map((item) =>
      item.productId === productId ? { ...item, quantity } : item
    );
    this._items.next(updatedItems);
  }

  clearCart(): void {
    this._items.next([]);
  }
}
