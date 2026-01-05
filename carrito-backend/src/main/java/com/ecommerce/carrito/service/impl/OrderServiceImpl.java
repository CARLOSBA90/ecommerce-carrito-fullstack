package com.ecommerce.carrito.service.impl;

import com.ecommerce.carrito.dto.order.OrderItemResponseDto;
import com.ecommerce.carrito.dto.order.OrderResponseDto;
import com.ecommerce.carrito.event.OrderCreatedEvent;
import com.ecommerce.carrito.exception.EntityNotFoundException;
import com.ecommerce.carrito.model.*;
import com.ecommerce.carrito.repository.CartRepository;
import com.ecommerce.carrito.repository.CustomerRepository;
import com.ecommerce.carrito.repository.OrderRepository;
import com.ecommerce.carrito.service.DiscountService;
import com.ecommerce.carrito.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final DiscountService discountService;

    @Override
    @Transactional
    public OrderResponseDto createOrder(Long customerId) {
        log.info("Creating order for customer ID: {}", customerId);

        Customer customer = findCustomerById(customerId);
        Cart cart = findCartByCustomerId(customerId);
        validateCartNotEmpty(cart);

        List<Discount> applicableDiscounts = discountService.findApplicableDiscounts(cart);
        BigDecimal subtotal = cart.getSubtotal();
        BigDecimal totalDiscount = discountService.calculateTotalDiscount(cart, applicableDiscounts);
        BigDecimal totalAmount = subtotal.subtract(totalDiscount).max(BigDecimal.ZERO);

        Order order = buildOrder(customer, cart, subtotal, totalDiscount, totalAmount, applicableDiscounts);
        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {} - Subtotal: {}, Discount: {}, Total: {}",
                savedOrder.getId(), subtotal, totalDiscount, totalAmount);

        clearCart(cart);
        eventPublisher.publishEvent(new OrderCreatedEvent(this, savedOrder));

        return mapToDto(savedOrder);
    }

    private Customer findCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));
    }

    private Cart findCartByCustomerId(Long customerId) {
        return cartRepository.findByCustomer_Id(customerId)
                .orElseThrow(() -> new EntityNotFoundException("No active cart found for customer"));
    }

    private void validateCartNotEmpty(Cart cart) {
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }
    }

    private Order buildOrder(Customer customer, Cart cart, BigDecimal subtotal,
            BigDecimal totalDiscount, BigDecimal totalAmount,
            List<Discount> discounts) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setAppliedCartType(cart.getType());
        order.setSubTotal(subtotal);
        order.setTotalDiscount(totalDiscount);
        order.setTotalAmount(totalAmount);
        order.setGeneratedAt(LocalDateTime.now());
        order.setAppliedDiscounts(new ArrayList<>(discounts));

        List<OrderItem> orderItems = buildOrderItems(order, cart);
        order.setItems(orderItems);

        return order;
    }

    private List<OrderItem> buildOrderItems(Order order, Cart cart) {
        return cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProduct().getId());
            orderItem.setProductDescription(cartItem.getProduct().getName());
            orderItem.setUnitPrice(cartItem.getProduct().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscountApplied(BigDecimal.ZERO);
            orderItem.setItemPromoCode(null);
            return orderItem;
        }).collect(Collectors.toList());
    }

    private void clearCart(Cart cart) {
        cart.getItems().clear();
        cart.setAppliedDiscounts(new ArrayList<>());
        cartRepository.save(cart);
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
