import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { LoginRequest, LoginResponse, AuthUser } from '../interfaces/auth.interface';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private readonly TOKEN_KEY = 'auth_token';
    private readonly USER_KEY = 'auth_user';
    private currentUserSubject = new BehaviorSubject<AuthUser | null>(this.getUserFromStorage());

    public currentUser$ = this.currentUserSubject.asObservable();

    constructor(private http: HttpClient) { }

    login(request: LoginRequest): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, request)
            .pipe(
                tap(response => {
                    const user: AuthUser = {
                        customerId: response.customerId,
                        username: response.username,
                        roles: response.roles,
                        token: response.token
                    };
                    localStorage.setItem(this.TOKEN_KEY, response.token);
                    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
                    this.currentUserSubject.next(user);
                })
            );
    }

    logout(): void {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
        this.currentUserSubject.next(null);
    }

    isAuthenticated(): boolean {
        return !!this.getToken();
    }

    getToken(): string | null {
        return localStorage.getItem(this.TOKEN_KEY);
    }

    getCustomerId(): number | null {
        const user = this.currentUserSubject.value;
        return user?.customerId || null;
    }

    getCurrentUser(): AuthUser | null {
        return this.currentUserSubject.value;
    }

    private getUserFromStorage(): AuthUser | null {
        const userStr = localStorage.getItem(this.USER_KEY);
        if (userStr) {
            try {
                return JSON.parse(userStr);
            } catch {
                return null;
            }
        }
        return null;
    }
}
