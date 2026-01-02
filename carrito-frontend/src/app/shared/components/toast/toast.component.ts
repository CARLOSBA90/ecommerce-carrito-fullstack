import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="message$ | async as msg" class="toast" [ngClass]="msg.type">
      {{ msg.text }}
    </div>
  `,
  styles: [`
    .toast {
      position: fixed;
      bottom: 20px;
      right: 20px; 
      padding: 12px 24px;
      background: #1a1a1a;
      color: white;
      border-radius: 8px;
      font-weight: 500;
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      z-index: 2000;
      animation: slideIn 0.3s ease, fadeOut 0.3s ease 2.7s;
    }
    .toast.success { background: #1a1a1a; }
    .toast.error { background: #ff4444; }
    
    @keyframes slideIn {
      from { transform: translateY(100%); opacity: 0; }
      to { transform: translateY(0); opacity: 1; }
    }
    @keyframes fadeOut {
      from { opacity: 1; }
      to { opacity: 0; }
    }
  `]
})
export class ToastComponent {
  message$;

  constructor(private toastService: ToastService) {
    this.message$ = this.toastService.message$;
  }
}
