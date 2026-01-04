package com.ecommerce.carrito.service.component;

import com.ecommerce.carrito.model.Cart;
import com.ecommerce.carrito.model.CartItem;
import com.ecommerce.carrito.model.Product;
import com.ecommerce.carrito.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartItemManager {
    private final CartItemRepository cartItemRepository;

    public CartItem findOrCreateCartItem(Cart cart, Product product) {
        return cartItemRepository.findByCartAndProduct_Id(cart, product.getId())
                .orElseGet(() -> {
                    CartItem newItem = CartItem.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(0)
                            .build();
                    cart.addItem(newItem);
                    return newItem;
                });
    }
}
