import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, catchError, tap, switchMap, forkJoin, throwError } from 'rxjs';
import { map } from 'rxjs/operators';
import { Cart } from '../interfaces/cart.interface';
import { OrderResponse } from '../interfaces/order-response.interface';
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
  private _initialized = false;

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
  ) { }

  toggleCart(): void {
    if (!this._isOpen.value) {
      this.loadCart();
    }
    this._isOpen.next(!this._isOpen.value);
  }

  openCart(): void {
    this.loadCart();
    this._isOpen.next(true);
  }

  closeCart(): void {
    this._isOpen.next(false);
  }

  init(): void {
    if (this._initialized) {
      return;
    }
    this._initialized = true;

    this.authService.currentUser$.pipe(
      switchMap(user => {
        if (user) {
          return this.uuidService.getSessionId().pipe(
            switchMap(sessionId => {
              if (sessionId) {
                console.log('Found local session, attempting merge:', sessionId);
                return this.assignSessionCartToCustomer().pipe(
                  catchError(err => {
                    console.warn('Merge failed (probably empty session), ignoring:', err);
                    return of(null);
                  })
                );
              }
              return of(null);
            }),
            tap(() => this.loadCart())
          );
        } else {
          console.log('User logged out, clearing session.');
          this.uuidService.clearSessionId();
          this.resetLocalCart();
          this.loadCart();
          return of(null);
        }
      })
    ).subscribe();
  }

  loadCart(): void {
    const customerId = this.authService.getCustomerId();

    if (customerId) {
      this.cartApiService.getCartByCustomer(customerId)
        .pipe(
          catchError((error) => {
            console.error('Error loading customer cart:', error);
            return of(null);
          })
        )
        .subscribe(cart => this._cart.next(cart));
    } else {
      this.uuidService.getSessionId().pipe(
        switchMap(sessionId => {
          if (sessionId) {
            return this.cartApiService.getCartBySession(sessionId).pipe(
              catchError(error => {
                if (error.status === 404 || error.status === 410 || error.status === 400) {
                  console.warn('Cart expired or invalid. Clearing session.');
                  this.uuidService.clearSessionId();
                }
                return of(null);
              })
            );
          }
          return of(null);
        }),
        catchError(() => of(null))
      ).subscribe(cart => this._cart.next(cart));
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
    this.getSessionIdentifier()
      .pipe(
        switchMap(sessionId => {
          if (!sessionId) throw new Error('No session ID available');
          return this.cartApiService.clearCart(sessionId);
        })
      )
      .subscribe({
        next: () => {
          this.resetLocalCart();
          this.loadCart();
          this.toastService.show('Carrito vaciado');
        },
        error: (err) => {
          console.error('Error clearing cart:', err);
          this.toastService.show('Error al vaciar carrito');
        }
      });
  }

  // Helper to reset local state (used after confirmation)
  resetLocalCart(): void {
    this._cart.next(null);
  }

  confirmCart(): Observable<OrderResponse> {
    const confirm$ = this.cartApiService.confirmCart().pipe(
      tap(() => {
        this.toastService.show('Carrito confirmado exitosamente');
        this.resetLocalCart();
        this.closeCart();
      })
    );

    if (!this.authService.isAuthenticated()) {
      return this.loginModalService.openModal().pipe(
        switchMap(() => this.assignSessionCartToCustomer()),
        switchMap(() => confirm$)
      );
    }

    return confirm$;
  }

  private assignSessionCartToCustomer(): Observable<Cart> {
    const customerId = this.authService.getCustomerId();
    if (!customerId) {
      return throwError(() => new Error('No customer ID found'));
    }

    return this.uuidService.getOrCreateSessionId().pipe(
      switchMap(sessionId =>
        this.cartApiService.assignCartToUser({ sessionId, customerId })
      ),
      tap((cart) => {
        this.uuidService.clearSessionId();
        this._cart.next(cart);
      })
    );
  }

  private getSessionIdentifier(): Observable<string> {
    const customerId = this.authService.getCustomerId();
    if (customerId) {
      return this.uuidService.getOrCreateSessionId();
    }
    return this.uuidService.getOrCreateSessionId();
  }
}
