export class MessageFormatterUtils {
    static formatTierEvaluationSuccess(tiersUpdated: number): string {
        if (tiersUpdated === 0) {
            return '✓ Evaluación completada. No se realizaron cambios de tier.';
        }

        const clientText = tiersUpdated > 1 ? 's actualizados' : ' actualizado';
        return `✓ Evaluación completada exitosamente.\n📊 ${tiersUpdated} cliente${clientText}.\nRevisa la consola del backend para ver el detalle.`;
    }

    static formatHttpError(status: number): string {
        if (status === 0) {
            return '❌ No se pudo conectar con el servidor. Verifica que el backend esté ejecutándose.';
        }

        if (status >= 500) {
            return `❌ Error del servidor (${status}). Revisa los logs del backend.`;
        }

        return `❌ Error al ejecutar la evaluación de tiers (${status}). Ver consola.`;
    }
}
