package com.ecommerce.carrito.service;

import com.ecommerce.carrito.dto.order.OrderResponseDto;

public interface IOrderService {
    OrderResponseDto createOrder(Long customerId);
}
