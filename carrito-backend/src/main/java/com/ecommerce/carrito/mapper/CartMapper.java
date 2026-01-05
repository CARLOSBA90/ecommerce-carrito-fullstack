package com.ecommerce.carrito.mapper;

import com.ecommerce.carrito.dto.ProductResponseDto;
import com.ecommerce.carrito.dto.cart.AppliedDiscountDto;
import com.ecommerce.carrito.dto.cart.CartItemDto;
import com.ecommerce.carrito.dto.cart.CartResponseDto;
import com.ecommerce.carrito.model.Cart;
import com.ecommerce.carrito.model.CartItem;
import com.ecommerce.carrito.model.Discount;
import com.ecommerce.carrito.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartMapper {

    private final ModelMapper modelMapper;
    private final DiscountService discountService;

    public CartResponseDto toResponseDto(Cart cart) {
        List<CartItemDto> itemDtos = cart.getItems().stream()
                .map(this::toCartItemDto)
                .collect(Collectors.toList());

        List<Discount> applicableDiscounts = discountService.findApplicableDiscounts(cart);
        BigDecimal subtotal = cart.getSubtotal();
        BigDecimal totalDiscounts = discountService.calculateTotalDiscount(cart, applicableDiscounts);
        BigDecimal totalAmount = subtotal.subtract(totalDiscounts).max(BigDecimal.ZERO);

        List<AppliedDiscountDto> appliedDiscountDtos = applicableDiscounts.stream()
                .map(d -> toAppliedDiscountDto(d, cart, subtotal))
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
                .subtotal(subtotal)
                .totalDiscounts(totalDiscounts)
                .totalAmount(totalAmount)
                .appliedDiscounts(appliedDiscountDtos)
                .build();
    }

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

    private AppliedDiscountDto toAppliedDiscountDto(Discount discount, Cart cart, BigDecimal subtotal) {
        BigDecimal amount = calculateIndividualDiscount(discount, cart, subtotal);
        return AppliedDiscountDto.builder()
                .code(discount.getCode())
                .name(discount.getName())
                .discountType(discount.getDiscountType().name())
                .discountAmount(amount)
                .build();
    }

    private BigDecimal calculateIndividualDiscount(Discount discount, Cart cart, BigDecimal subtotal) {
        return switch (discount.getDiscountType()) {
            case PERCENTAGE -> subtotal.multiply(discount.getValue())
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            case FIXED -> discount.getValue() != null ? discount.getValue() : BigDecimal.ZERO;
            case FREE_PRODUCT -> cart.getItems().stream()
                    .map(i -> i.getProduct().getPrice())
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        };
    }

    private Long extractCustomerId(Cart cart) {
        return cart.getCustomer() != null ? cart.getCustomer().getId() : null;
    }

    private String extractCustomerName(Cart cart) {
        return cart.getCustomer() != null ? cart.getCustomer().getFullName() : null;
    }
}
