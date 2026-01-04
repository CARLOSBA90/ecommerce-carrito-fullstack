package com.ecommerce.carrito.validator;

import com.ecommerce.carrito.exception.InsufficientStockException;
import com.ecommerce.carrito.model.Product;
import org.springframework.stereotype.Component;

@Component
public class StockValidator {

    /**
     * Validates that a product has sufficient stock for the requested quantity.
     *
     * @param product           the product to check stock for
     * @param requestedQuantity the quantity being requested
     * @throws InsufficientStockException if stock is insufficient
     */
    public void validateStock(Product product, int requestedQuantity) {
        if (product.getStock() < requestedQuantity) {
            throw new InsufficientStockException(
                    String.format(
                            "Stock insuficiente para %s. Disponible: %d, Solicitado: %d",
                            product.getName(),
                            product.getStock(),
                            requestedQuantity));
        }
    }

    /**
     * Checks if a product has sufficient stock without throwing an exception.
     *
     * @param product  the product to check
     * @param quantity the quantity to check
     * @return true if stock is sufficient, false otherwise
     */
    public boolean hasStock(Product product, int quantity) {
        return product.getStock() >= quantity;
    }

    /**
     * Gets the available stock for a product.
     *
     * @param product the product
     * @return the available stock quantity
     */
    public int getAvailableStock(Product product) {
        return product.getStock();
    }
}
