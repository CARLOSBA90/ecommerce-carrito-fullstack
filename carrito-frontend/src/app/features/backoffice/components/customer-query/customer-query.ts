import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ConfirmationModalComponent } from '../../../../shared/components/confirmation-modal/confirmation-modal.component';
import { CustomerReportService, CustomerReportItem, ReportType } from '../../services/customer-report.service';
import { TierManagementService } from '../../services/tier-management.service';
import { FormValidationUtils } from '../../../../shared/utils/form-validation.utils';
import { MessageFormatterUtils } from '../../../../shared/utils/message-formatter.utils';

@Component({
  selector: 'app-customer-query',
  standalone: true,
  imports: [CommonModule, FormsModule, ConfirmationModalComponent],
  templateUrl: './customer-query.html',
  styleUrl: './customer-query.css',
})
export class CustomerQueryComponent {
  // Filters
  selectedMonth: number = new Date().getMonth() + 1 || 1;
  selectedYear: number = new Date().getFullYear();
  selectedType: ReportType = 'ALL_CHANGES';

  // State
  isLoading = false;
  isEvaluating = false;
  error: string | null = null;
  items: CustomerReportItem[] = [];
  evaluationMessage: string | null = null;
  showConfirmModal = false;

  // UI Data
  readonly months = [
    { value: 1, label: 'Enero' },
    { value: 2, label: 'Febrero' },
    { value: 3, label: 'Marzo' },
    { value: 4, label: 'Abril' },
    { value: 5, label: 'Mayo' },
    { value: 6, label: 'Junio' },
    { value: 7, label: 'Julio' },
    { value: 8, label: 'Agosto' },
    { value: 9, label: 'Septiembre' },
    { value: 10, label: 'Octubre' },
    { value: 11, label: 'Noviembre' },
    { value: 12, label: 'Diciembre' },
  ];

  readonly reportTypes = [
    { value: 'ALL_CHANGES' as ReportType, label: 'Todos los Cambios' },
    { value: 'NEW_VIP' as ReportType, label: 'Nuevos VIPs' },
    { value: 'LOST_VIP' as ReportType, label: 'VIPs Perdidos' },
    { value: 'CURRENT_VIP' as ReportType, label: 'VIPs Actuales' },
  ];

  constructor(
    private customerReportService: CustomerReportService,
    private tierManagementService: TierManagementService
  ) { }

  fetchReport(): void {
    const validationError = FormValidationUtils.validateMonthYear(this.selectedMonth, this.selectedYear);
    if (validationError) {
      this.error = validationError;
      return;
    }

    this.isLoading = true;
    this.error = null;
    this.items = [];

    this.customerReportService
      .getCustomerReport(this.selectedType, this.selectedMonth, this.selectedYear)
      .subscribe({
        next: (items) => {
          this.items = items;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('SOAP Error', err);
          this.error = err.parsedMessage
            ? `Error SOAP: ${err.parsedMessage}`
            : 'Error al consultar el servicio SOAP. Ver consola.';
          this.isLoading = false;
        }
      });
  }

  triggerTierEvaluation(): void {
    this.showConfirmModal = true;
  }

  onConfirmEvaluation(): void {
    this.showConfirmModal = false;
    this.executeEvaluation();
  }

  onCancelEvaluation(): void {
    this.showConfirmModal = false;
  }

  private executeEvaluation(): void {
    this.isEvaluating = true;
    this.evaluationMessage = null;
    this.error = null;

    this.tierManagementService.evaluateTiers().subscribe({
      next: (response) => {
        this.evaluationMessage = MessageFormatterUtils.formatTierEvaluationSuccess(response.tiersUpdated);
        this.isEvaluating = false;
        setTimeout(() => this.evaluationMessage = null, 8000);
      },
      error: (err) => {
        console.error('Tier Evaluation Error', err);
        this.error = MessageFormatterUtils.formatHttpError(err.status);
        this.isEvaluating = false;
      }
    });
  }
}
