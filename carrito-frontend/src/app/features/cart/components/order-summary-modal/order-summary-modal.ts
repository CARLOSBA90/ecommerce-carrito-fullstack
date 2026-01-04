import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { OrderResponse } from '../../interfaces/order-response.interface';

@Component({
  selector: 'app-order-summary-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-summary-modal.html',
  styleUrl: './order-summary-modal.css'
})
export class OrderSummaryModalComponent {
  @Input() order: OrderResponse | null = null;
  @Input() isOpen = false;
  @Output() close = new EventEmitter<void>();

  constructor(private router: Router) { }

  onAccept(): void {
    this.close.emit();
    this.router.navigate(['/']); // Return to Home
  }
}
