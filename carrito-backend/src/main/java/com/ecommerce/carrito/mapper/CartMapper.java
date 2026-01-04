package com.ecommerce.carrito.mapper;

import com.ecommerce.carrito.dto.ProductResponseDto;
import com.ecommerce.carrito.dto.cart.CartItemDto;
import com.ecommerce.carrito.dto.cart.CartResponseDto;
import com.ecommerce.carrito.model.Cart;
import com.ecommerce.carrito.model.CartItem;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper component for converting Cart entities to DTOs.
 * Handles the transformation of Cart and CartItem entities to their response
 * DTOs.
 */
@Component
@RequiredArgsConstructor
public class CartMapper {

    private final ModelMapper modelMapper;

    /**
     * Maps a Cart entity to a CartResponseDto.
     *
     * @param cart the cart entity to map
     * @return the mapped CartResponseDto
     */
    public CartResponseDto toResponseDto(Cart cart) {
        List<CartItemDto> itemDtos = cart.getItems().stream()
                .map(this::toCartItemDto)
                .collect(Collectors.toList());

        return CartResponseDto.builder()
                .id(cart.getId())
                .sessionId(cart.getSessionId())
                .customerId(extractCustomerId(cart))
                .customerName(extractCustomerName(cart))
                .items(itemDtos)
                .type(cart.getType())
                .creationDate(cart.getCreationDate())
                .totalProductCount(cart.getTotalProductCount())
                .subtotal(cart.getSubtotal())
                .build();
    }

    /**
     * Maps a CartItem entity to a CartItemDto.
     *
     * @param cartItem the cart item entity to map
     * @return the mapped CartItemDto
     */
    public CartItemDto toCartItemDto(CartItem cartItem) {
        ProductResponseDto productDto = modelMapper.map(
                cartItem.getProduct(),
                ProductResponseDto.class);

        return CartItemDto.builder()
                .id(cartItem.getId())
                .product(productDto)
                .quantity(cartItem.getQuantity())
                .build();
    }

    private Long extractCustomerId(Cart cart) {
        return cart.getCustomer() != null ? cart.getCustomer().getId() : null;
    }

    private String extractCustomerName(Cart cart) {
        return cart.getCustomer() != null ? cart.getCustomer().getFullName() : null;
    }
}
