package com.ecommerce.carrito.dto.cart;

import com.ecommerce.carrito.dto.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {

    private Long id;
    private ProductResponseDto product;
    private Integer quantity;
}
