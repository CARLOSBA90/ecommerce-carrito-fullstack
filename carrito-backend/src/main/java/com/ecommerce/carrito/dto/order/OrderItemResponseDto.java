package com.ecommerce.carrito.dto.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountApplied;
    private BigDecimal finalLinePrice;
}
