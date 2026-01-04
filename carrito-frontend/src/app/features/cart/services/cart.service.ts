import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, catchError, tap, switchMap, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { Cart } from '../interfaces/cart.interface';
import { ToastService } from '../../../shared/services/toast.service';
import { CartApiService } from './cart-api.service';
import { UuidService } from '../../../shared/services/uuid.service';
import { AuthService } from '../../auth/services/auth.service';
import { LoginModalService } from '../../../shared/services/login-modal.service';
import { Product } from '../../products/services/products.service';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private _isOpen = new BehaviorSubject<boolean>(false);
  private _cart = new BehaviorSubject<Cart | null>(null);

  // Public Observables
  isOpen$ = this._isOpen.asObservable();
  cart$ = this._cart.asObservable();

  count$: Observable<number> = this.cart$.pipe(
    map((cart) => cart?.totalProductCount || 0)
  );

  total$: Observable<number> = this.cart$.pipe(
    map((cart) => cart?.subtotal || 0)
  );

  items$ = this.cart$.pipe(
    map((cart) => cart?.items || [])
  );

  constructor(
    private toastService: ToastService,
    private cartApiService: CartApiService,
    private uuidService: UuidService,
    private authService: AuthService,
    private loginModalService: LoginModalService
  ) {
    // Removed automatic cart loading for optimization
    // Cart will be loaded only when needed (on first add or when user opens cart)
  }

  toggleCart(): void {
    // Load cart when opening if not already loaded and user has items
    if (!this._isOpen.value && !this._cart.value) {
      this.loadCart();
    }
    this._isOpen.next(!this._isOpen.value);
  }

  openCart(): void {
    // Load cart when opening if not already loaded
    if (!this._cart.value) {
      this.loadCart();
    }
    this._isOpen.next(true);
  }

  closeCart(): void {
    this._isOpen.next(false);
  }

  loadCart(): void {
    const customerId = this.authService.getCustomerId();

    if (customerId) {
      // Load customer cart
      this.cartApiService.getCartByCustomer(customerId)
        .pipe(
          catchError(() => of(null))
        )
        .subscribe(cart => this._cart.next(cart));
    } else {
      // Load session cart
      this.uuidService.getOrCreateSessionId()
        .pipe(
          switchMap(sessionId => this.cartApiService.getCartBySession(sessionId)),
          catchError(() => of(null))
        )
        .subscribe(cart => this._cart.next(cart));
    }
  }

  addToCart(product: Product, quantity: number = 1): void {
    const customerId = this.authService.getCustomerId();

    if (customerId) {
      // For authenticated users, use customerId directly
      const request = {
        customerId,
        productId: product.id,
        quantity
      };

      this.cartApiService.addItemToCart(request)
        .subscribe({
          next: (cart) => {
            this._cart.next(cart);
            this.toastService.show('Producto agregado al carrito');
          },
          error: (err) => {
            console.error('Error adding to cart:', err);
            this.toastService.show('Error al agregar producto');
          }
        });
    } else {
      // For anonymous users, get sessionId from backend first
      this.uuidService.getOrCreateSessionId()
        .pipe(
          switchMap(sessionId => {
            const request = {
              sessionId,
              productId: product.id,
              quantity
            };
            return this.cartApiService.addItemToCart(request);
          })
        )
        .subscribe({
          next: (cart) => {
            this._cart.next(cart);
            this.toastService.show('Producto agregado al carrito');
          },
          error: (err) => {
            console.error('Error adding to cart:', err);
            this.toastService.show('Error al agregar producto');
          }
        });
    }
  }

  removeFromCart(productId: number): void {
    this.getSessionIdentifier()
      .pipe(
        switchMap(sessionId => {
          if (!sessionId) throw new Error('No session ID available');
          return this.cartApiService.removeItem(sessionId, productId);
        })
      )
      .subscribe({
        next: (cart) => {
          this._cart.next(cart);
          this.toastService.show('Producto eliminado del carrito');
        },
        error: (err) => {
          console.error('Error removing from cart:', err);
          this.toastService.show('Error al eliminar producto');
        }
      });
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return;
    }

    this.getSessionIdentifier()
      .pipe(
        switchMap(sessionId => {
          if (!sessionId) throw new Error('No session ID available');
          return this.cartApiService.updateQuantity(sessionId, productId, quantity);
        })
      )
      .subscribe({
        next: (cart) => {
          this._cart.next(cart);
        },
        error: (err) => {
          console.error('Error updating quantity:', err);
          this.toastService.show('Error al actualizar cantidad');
        }
      });
  }

  clearCart(): void {
    this._cart.next(null);
  }

  confirmCart(): void {
    // Check if user is authenticated
    if (!this.authService.isAuthenticated()) {
      // Open login modal and wait for login
      this.loginModalService.openModal().subscribe(() => {
        // After successful login, assign session cart to customer and reload
        this.assignSessionCartToCustomer();
      });
      return;
    }

    // User is authenticated, proceed with confirmation
    this.proceedWithConfirmation();
  }

  private assignSessionCartToCustomer(): void {
    const customerId = this.authService.getCustomerId();
    if (!customerId) return;

    this.uuidService.getOrCreateSessionId()
      .pipe(
        switchMap(sessionId =>
          this.cartApiService.assignCartToUser({ sessionId, customerId })
        ),
        tap(() => {
          this.uuidService.clearSessionId();
          this.loadCart(); // Reload cart with promotions
        }),
        switchMap(() => this.cartApiService.getCartByCustomer(customerId))
      )
      .subscribe({
        next: (cart) => {
          this._cart.next(cart);
          this.proceedWithConfirmation();
        },
        error: (err) => {
          console.error('Error assigning cart:', err);
          this.toastService.show('Error al asignar carrito');
        }
      });
  }

  private proceedWithConfirmation(): void {
    this.cartApiService.confirmCart()
      .subscribe({
        next: (message) => {
          this.toastService.show('Carrito confirmado exitosamente');
          this.clearCart();
          this.closeCart();
          console.log(message);
        },
        error: (err) => {
          console.error('Error confirming cart:', err);
          this.toastService.show('Error al confirmar carrito');
        }
      });
  }

  private getSessionIdentifier(): Observable<string> {
    const customerId = this.authService.getCustomerId();
    if (customerId) {
      // For authenticated users, we still need to get/use session for operations
      // This might need adjustment based on your backend logic
      return this.uuidService.getOrCreateSessionId();
    }
    return this.uuidService.getOrCreateSessionId();
  }
}
