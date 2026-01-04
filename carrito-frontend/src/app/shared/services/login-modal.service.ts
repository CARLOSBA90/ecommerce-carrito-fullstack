import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class LoginModalService {
    private isOpenSubject = new BehaviorSubject<boolean>(false);
    private loginSuccessSubject = new Subject<void>();

    public isOpen$ = this.isOpenSubject.asObservable();
    public loginSuccess$ = this.loginSuccessSubject.asObservable();

    openModal(): Observable<void> {
        this.isOpenSubject.next(true);
        return this.loginSuccess$;
    }

    closeModal(): void {
        this.isOpenSubject.next(false);
    }

    notifyLoginSuccess(): void {
        this.loginSuccessSubject.next();
        this.closeModal();
    }
}
