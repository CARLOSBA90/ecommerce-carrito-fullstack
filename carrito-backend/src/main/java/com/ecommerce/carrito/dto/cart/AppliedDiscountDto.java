package com.ecommerce.carrito.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppliedDiscountDto {
    private String code;
    private String name;
    private String discountType;
    private BigDecimal discountAmount;
}
