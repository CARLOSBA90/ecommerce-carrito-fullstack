package com.ecommerce.carrito.dto.order;

import com.ecommerce.carrito.model.enums.CartType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponseDto {
    private Long id;
    private Long customerId;
    private BigDecimal totalAmount;
    private BigDecimal totalDiscount;
    private int totalItems;
    private CartType cartType;
    private LocalDateTime generatedAt;
    private List<OrderItemResponseDto> items;
}
