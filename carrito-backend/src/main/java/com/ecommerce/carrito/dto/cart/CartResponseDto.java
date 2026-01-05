package com.ecommerce.carrito.dto.cart;

import com.ecommerce.carrito.model.enums.CartType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDto {

    private Long id;
    private String sessionId;
    private Long customerId;
    private String customerName;
    private List<CartItemDto> items;
    private CartType type;
    private java.time.LocalDateTime creationDate;
    private Integer totalProductCount;
    private BigDecimal subtotal;
    private BigDecimal totalDiscounts;
    private BigDecimal totalAmount;
    private List<AppliedDiscountDto> appliedDiscounts;
}
