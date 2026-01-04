package com.ecommerce.carrito.dto.cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignCartToUserRequestDto {

    @NotBlank(message = "El sessionId es obligatorio")
    private String sessionId;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long customerId;
}
