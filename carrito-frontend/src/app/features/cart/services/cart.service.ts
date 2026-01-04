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
    // Always load cart when opening to ensure fresh data
    if (!this._isOpen.value) {
      this.loadCart();
    }
    this._isOpen.next(!this._isOpen.value);
  }

  openCart(): void {
    // Always load cart when opening
    this.loadCart();
    this._isOpen.next(true);
  }

  closeCart(): void {
    this._isOpen.next(false);
  }

  init(): void {
    // Subscribe to auth state changes
    this.authService.currentUser$.pipe(
      switchMap(user => {
        if (user) {
          // USER LOGGED IN
          // Check if there is a local session to merge
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
          // USER LOGGED OUT
          console.log('User logged out, clearing session.');
          this.uuidService.clearSessionId();
          this.resetLocalCart();
          this.loadCart(); // Will load empty/new session
          return of(null);
        }
      })
    ).subscribe();
  }

  loadCart(): void {
    const customerId = this.authService.getCustomerId();

    if (customerId) {
      // Load customer cart
      this.cartApiService.getCartByCustomer(customerId)
        .pipe(
          catchError((error) => {
            console.error('Error loading customer cart:', error);
            // If customer cart error, usually don't clear session unless specific logic.
            // But for safety, we set cart to null.
            return of(null);
          })
        )
        .subscribe(cart => this._cart.next(cart));
    } else {
      // Check if we have a session ID
      this.uuidService.getSessionId().pipe(
        // If no session ID, just return null (don't create one yet until user interacts?)
        // User said: "al entrar a la pagina verificar si existe id de carrito... mostrar contador"
        // If we have an ID stored, we must check it.
        // If we DONT have an ID stored, we do nothing (cart is empty)?
        // Current UuidService.getOrCreateSessionId() creates one if missing locally?
        // Wait, uuidService has getSessionId() (observable of string).
        // If local storage empty -> checks backend?
        // If I use getOrCreateSessionId(), it creates one.
        // I should usage "getSessionId" which might be empty?
        // uuidService.getOrCreateSessionId() logic:
        // 1. Check local. If exists -> return.
        // 2. If not -> call backend generate -> store -> return.
        // If I call this on init, I create a session for EVERY visitor.
        // This uses backend resources (UUID generation).
        // Is this desired?
        // User said: "mostrar contador y lo necesario para q el usuario recupere los datos"
        // If stored ID exists, check it.
        // If NO stored ID, cart is empty. No need to create session yet.
        // So I need a way to check *existing* session without creating new one.
        switchMap(sessionId => {
          if (sessionId) {
            return this.cartApiService.getCartBySession(sessionId).pipe(
              catchError(error => {
                // If 404 or similar, it means expired or invalid.
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
          // Reload cart to get empty state from backend (optional, but cleaner if backend returns empty cart structure)
          // But clearCart returns void. So we just reset local state or fetch again.
          // Best to just set to empty cart structure?
          // Or reloadCart makes a call.
          // If we want to show 'Empty Cart' UI, resetting to null might show loading skeleton?
          // Use reloadCart() to fetch fresh empty state.
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
        this.clearCart();
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
        this._cart.next(cart); // Update local state directly with returned cart
        // this.loadCart(); // No need to reload entire cart again if assign returns it
      })
    );
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
