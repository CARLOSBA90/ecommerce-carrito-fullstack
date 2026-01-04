package com.ecommerce.carrito.util;

public final class ValidationUtil {

    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Validates that an ID is not null and greater than zero.
     *
     * @param id         the ID to validate
     * @param entityName the name of the entity for error messages
     * @throws IllegalArgumentException if ID is invalid
     */
    public static void validateId(Long id, String entityName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    String.format("ID no válido para %s", entityName));
        }
    }

    /**
     * Validates that a session ID is not null or blank.
     *
     * @param sessionId the session ID to validate
     * @throws IllegalArgumentException if session ID is invalid
     */
    public static void validateSessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("El sessionId no puede estar vacío");
        }
    }

    /**
     * Validates that a quantity is positive (greater than zero).
     *
     * @param quantity the quantity to validate
     * @throws IllegalArgumentException if quantity is invalid
     */
    public static void validatePositiveQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
    }
}
