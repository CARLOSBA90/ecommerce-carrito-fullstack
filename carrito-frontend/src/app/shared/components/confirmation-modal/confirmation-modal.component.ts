import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-confirmation-modal',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div *ngIf="isOpen" class="modal-backdrop" (click)="onBackdropClick($event)">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ title }}</h2>
          <p>{{ message }}</p>
        </div>
        <div class="modal-actions">
          <button class="btn-cancel" (click)="onCancel()">Cancelar</button>
          <button class="btn-confirm" (click)="onConfirm()">Confirmar</button>
        </div>
      </div>
    </div>
  `,
    styles: [`
    .modal-backdrop {
      position: fixed;
      top: 0; left: 0; right: 0; bottom: 0;
      background: rgba(0, 0, 0, 0.6);
      backdrop-filter: blur(4px);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 10000;
      animation: fadeIn 0.2s ease;
    }
    @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

    .modal-content {
      background: #ffffff;
      border-radius: 24px;
      padding: 40px;
      max-width: 400px;
      width: 90%;
      text-align: center;
      box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
      animation: slideUp 0.3s ease;
    }
    @keyframes slideUp { from { transform: translateY(30px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }

    .modal-header h2 {
      font-size: 24px;
      margin: 0 0 12px 0;
      color: #1a1a1a;
      font-weight: 700;
    }
    .modal-header p {
      font-size: 16px;
      color: #666;
      margin: 0 0 32px 0;
      line-height: 1.5;
    }

    .modal-actions {
      display: flex;
      gap: 16px;
      justify-content: center;
    }

    button {
      padding: 14px 24px;
      border-radius: 12px;
      font-size: 15px;
      font-weight: 600;
      cursor: pointer;
      border: none;
      transition: all 0.2s ease;
      flex: 1;
    }

    .btn-cancel {
      background: #f1f5f9;
      color: #64748b;
    }
    .btn-cancel:hover {
      background: #e2e8f0;
      color: #475569;
    }

    .btn-confirm {
      background: #667eea;
      color: white;
    }
    .btn-confirm:hover {
      background: #5a67d8;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
    }
  `]
})
export class ConfirmationModalComponent {
    @Input() isOpen = false;
    @Input() title = 'Confirmación';
    @Input() message = '¿Estás seguro?';

    @Output() confirm = new EventEmitter<void>();
    @Output() cancel = new EventEmitter<void>();

    onConfirm(): void {
        this.confirm.emit();
    }

    onCancel(): void {
        this.cancel.emit();
    }

    onBackdropClick(event: MouseEvent): void {
        if (event.target === event.currentTarget) {
            this.cancel.emit();
        }
    }
}
