import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class UuidService {
    private readonly SESSION_ID_KEY = 'cart_session_id';

    constructor(private http: HttpClient) { }

    getSessionId(): Observable<string | null> {
        const existingSessionId = sessionStorage.getItem(this.SESSION_ID_KEY);
        return of(existingSessionId);
    }

    getOrCreateSessionId(): Observable<string> {
        const existingSessionId = sessionStorage.getItem(this.SESSION_ID_KEY);

        if (existingSessionId) {
            return of(existingSessionId);
        }

        // Request new session ID from backend
        return this.http.get(`${environment.apiUrl}/cart/generate-session`, { responseType: 'text' })
            .pipe(
                tap(sessionId => {
                    sessionStorage.setItem(this.SESSION_ID_KEY, sessionId);
                }),
                catchError(error => {
                    console.error('Error generating session ID from backend:', error);
                    return throwError(() => new Error('No se pudo obtener el ID de sesión del servidor'));
                })
            );
    }

    clearSessionId(): void {
        sessionStorage.removeItem(this.SESSION_ID_KEY);
    }
}
