import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Cart } from '../interfaces/cart.interface';

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
        return this.http.get<Cart>(`${this.apiUrl}/customer/${customerId}`);
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

    confirmCart(): Observable<string> {
        return this.http.post(`${this.apiUrl}/confirm`, {}, { responseType: 'text' });
    }

    assignCartToUser(request: AssignCartToUserRequest): Observable<Cart> {
        return this.http.post<Cart>(`${this.apiUrl}/assign`, request);
    }
}
