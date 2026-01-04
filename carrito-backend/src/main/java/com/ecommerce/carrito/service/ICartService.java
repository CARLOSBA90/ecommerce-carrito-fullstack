package com.ecommerce.carrito.service;

import com.ecommerce.carrito.dto.cart.AddItemToCartRequestDto;
import com.ecommerce.carrito.dto.cart.AssignCartToUserRequestDto;
import com.ecommerce.carrito.dto.cart.CartResponseDto;

public interface ICartService {

    CartResponseDto getCartBySessionId(String sessionId);

    CartResponseDto getCartByCustomerId(Long customerId);

    CartResponseDto addItemToCart(AddItemToCartRequestDto request);

    CartResponseDto assignCartToUser(AssignCartToUserRequestDto request);

    CartResponseDto removeItemFromCart(String sessionId, Long customerId, Long productId);

    CartResponseDto updateItemQuantity(String sessionId, Long customerId, Long productId, Integer quantity);

    void clearCart(String sessionId, Long customerId);
}
