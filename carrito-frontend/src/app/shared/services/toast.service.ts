import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface ToastMessage {
    text: string;
    type: 'success' | 'error' | 'info';
}

@Injectable({
    providedIn: 'root',
})
export class ToastService {
    private _message = new BehaviorSubject<ToastMessage | null>(null);
    message$ = this._message.asObservable();

    show(text: string, type: 'success' | 'error' | 'info' = 'success', duration = 4000): void {
        this._message.next({ text, type });
        setTimeout(() => {
            this._message.next(null);
        }, duration);
    }
}
