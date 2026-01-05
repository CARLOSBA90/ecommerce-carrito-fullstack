export class FormValidationUtils {
    static isValidMonth(month: number): boolean {
        return month >= 1 && month <= 12;
    }

    static isValidYear(year: number): boolean {
        return year >= 2000 && year <= 2100;
    }

    static validateMonthYear(month: number, year: number): string | null {
        if (!month || !FormValidationUtils.isValidMonth(month)) {
            return 'Por favor selecciona un mes válido (1-12)';
        }

        if (!year || !FormValidationUtils.isValidYear(year)) {
            return 'Por favor ingresa un año válido';
        }

        return null;
    }
}
