package com.ecommerce.carrito.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddItemToCartRequestDto {

    private String sessionId;

    private Long customerId;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    @Positive(message = "La cantidad debe ser mayor a cero")
    @Builder.Default
    private Integer quantity = 1;
}
