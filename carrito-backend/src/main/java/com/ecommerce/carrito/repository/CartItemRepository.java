package com.ecommerce.carrito.repository;

import com.ecommerce.carrito.model.Cart;
import com.ecommerce.carrito.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct_Id(Cart cart, Long productId);
}
