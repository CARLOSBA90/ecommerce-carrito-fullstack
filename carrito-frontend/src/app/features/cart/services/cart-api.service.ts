import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Cart } from '../interfaces/cart.interface';
import { OrderResponse } from '../interfaces/order-response.interface';

export interface AddItemToCartRequest {
    sessionId?: string;
    customerId?: number;
    productId: number;
    quantity: number;
}

export interface AssignCartToUserRequest {
    sessionId: string;
    customerId: number;
}

@Injectable({
    providedIn: 'root'
})
export class CartApiService {
    private apiUrl = `${environment.apiUrl}/cart`;

    constructor(private http: HttpClient) { }

    getCartBySession(sessionId: string): Observable<Cart> {
        return this.http.get<Cart>(`${this.apiUrl}/session/${sessionId}`);
    }

    getCartByCustomer(customerId: number): Observable<Cart> {
        // ID is now ignored in favor of the token's identity
        return this.http.get<Cart>(`${this.apiUrl}/me`);
    }

    addItemToCart(request: AddItemToCartRequest): Observable<Cart> {
        return this.http.post<Cart>(`${this.apiUrl}/items`, request);
    }

    removeItem(sessionId: string, productId: number): Observable<Cart> {
        return this.http.delete<Cart>(`${this.apiUrl}/${sessionId}/items/${productId}`);
    }

    updateQuantity(sessionId: string, productId: number, quantity: number): Observable<Cart> {
        return this.http.put<Cart>(`${this.apiUrl}/${sessionId}/items/${productId}/quantity/${quantity}`, {});
    }

    clearCart(sessionId: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}`, { params: { sessionId } });
    }

    confirmCart(): Observable<OrderResponse> {
        return this.http.post<OrderResponse>(`${this.apiUrl}/confirm`, {});
    }

    assignCartToUser(request: AssignCartToUserRequest): Observable<Cart> {
        return this.http.post<Cart>(`${this.apiUrl}/assign`, request);
    }
}
