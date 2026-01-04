package com.ecommerce.carrito.resolver;

import com.ecommerce.carrito.model.Customer;
import com.ecommerce.carrito.model.enums.CartType;
import org.springframework.stereotype.Component;

/**
 * Resolver component for determining the appropriate CartType based on customer
 * tier.
 * Centralizes the business logic for cart type determination.
 */
@Component
public class CartTypeResolver {

    /**
     * Resolves the appropriate CartType based on the customer's tier.
     * Returns GUEST type if customer is null.
     *
     * @param customer the customer for which to resolve the cart type
     * @return the appropriate CartType for the customer
     */
    public CartType resolveCartType(Customer customer) {
        if (customer == null) {
            return CartType.GUEST;
        }

        return switch (customer.getTier()) {
            case VIP -> CartType.VIP;
            case COMMON -> CartType.COMMON;
            default -> CartType.COMMON;
        };
    }
}
