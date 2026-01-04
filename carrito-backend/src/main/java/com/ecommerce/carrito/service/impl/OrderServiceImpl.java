package com.ecommerce.carrito.service.impl;

import com.ecommerce.carrito.dto.order.OrderItemResponseDto;
import com.ecommerce.carrito.dto.order.OrderResponseDto;
import com.ecommerce.carrito.event.OrderCreatedEvent;
import com.ecommerce.carrito.exception.EntityNotFoundException;
import com.ecommerce.carrito.model.*;
import com.ecommerce.carrito.repository.CartRepository;
import com.ecommerce.carrito.repository.CustomerRepository;
import com.ecommerce.carrito.repository.OrderRepository;
import com.ecommerce.carrito.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements IOrderService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public OrderResponseDto createOrder(Long customerId) {
        log.info("Creating order for customer ID: {}", customerId);

        // 1. Validate Customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));

        // 2. Get Customer Cart
        Cart cart = cartRepository.findByCustomer_Id(customerId)
                .orElseThrow(() -> new EntityNotFoundException("No active cart found for customer"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }

        // 3. Create Order from Cart
        Order order = new Order();
        order.setCustomer(customer);
        order.setAppliedCartType(cart.getType());
        order.setSubTotal(cart.getSubtotal());
        order.setTotalDiscount(cart.getTotalDiscounts());
        order.setTotalAmount(cart.getTotalPrice());
        order.setGeneratedAt(LocalDateTime.now());
        order.setAppliedDiscounts(new ArrayList<>(cart.getAppliedDiscounts())); // Copy discounts

        // 4. Create OrderItems
        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProduct().getId());
            orderItem.setProductDescription(cartItem.getProduct().getName());
            orderItem.setUnitPrice(cartItem.getProduct().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());

            // Calculate Item Discount (if we tracked per-item discount, currently cart
            // stores total)
            // For now assuming 0 per item or distributing?
            // User requested: "post valida... solo validar q exista customer".
            // Since CartItem doesn't store applied discount value (only Cart does global),
            // we will set discountApplied to 0 or derive if possible.
            // Requirement says "generar parte de confirmar carrito vinculada a entidad
            // order".
            // We'll set 0 for item level discount for now unless we refactor CartItem.
            orderItem.setDiscountApplied(java.math.BigDecimal.ZERO);
            orderItem.setItemPromoCode(null);

            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        // 5. Save Order (Cascade saves items)
        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {}", savedOrder.getId());

        // 6. Clear Cart (Remove items and reset totals)
        // Or delete cart? Requirement: "limpiar carts".
        // Keeping the cart entity but emptying it is safer for session reuse.
        cart.getItems().clear();
        cart.setAppliedDiscounts(new ArrayList<>());
        cartRepository.save(cart);

        // 7. Publish Event for TierService
        eventPublisher.publishEvent(new OrderCreatedEvent(this, savedOrder));

        return mapToDto(savedOrder);
    }

    private OrderResponseDto mapToDto(Order order) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .totalAmount(order.getTotalAmount())
                .totalDiscount(order.getTotalDiscount())
                .totalItems(order.getItems().stream().mapToInt(OrderItem::getQuantity).sum())
                .cartType(order.getAppliedCartType())
                .generatedAt(order.getGeneratedAt())
                .items(order.getItems().stream().map(this::mapItemToDto).collect(Collectors.toList()))
                .build();
    }

    private OrderItemResponseDto mapItemToDto(OrderItem item) {
        return OrderItemResponseDto.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .discountApplied(item.getDiscountApplied())
                .finalLinePrice(item.getFinalLinePrice())
                .build();
    }
}
