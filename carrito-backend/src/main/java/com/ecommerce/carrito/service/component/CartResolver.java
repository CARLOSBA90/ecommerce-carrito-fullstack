package com.ecommerce.carrito.service.component;

import com.ecommerce.carrito.exception.EntityNotFoundException;
import com.ecommerce.carrito.model.Cart;
import com.ecommerce.carrito.model.Customer;
import com.ecommerce.carrito.model.enums.CartType;
import com.ecommerce.carrito.repository.CartRepository;
import com.ecommerce.carrito.repository.CustomerRepository;
import com.ecommerce.carrito.resolver.CartTypeResolver;
import com.ecommerce.carrito.util.UuidUtil;
import com.ecommerce.carrito.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartResolver {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final CartTypeResolver cartTypeResolver;

    public Cart findCart(String sessionId, Long customerId) {
        if (customerId != null) {
            Cart cart = cartRepository.findByCustomer_Id(customerId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "No existe un carrito para el cliente con ID: " + customerId));
            checkExpiration(cart);
            return cart;
        }

        ValidationUtil.validateSessionId(sessionId);
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe un carrito con sessionId: " + sessionId));

        checkExpiration(cart);
        return cart;
    }

    public Cart getOrCreateCart(String sessionId, Long customerId) {
        if (customerId != null) {
            return cartRepository.findByCustomer_Id(customerId)
                    .map(cart -> {
                        try {
                            checkExpiration(cart);
                            return cart;
                        } catch (EntityNotFoundException e) {
                            return createCartForCustomer(customerId);
                        }
                    })
                    .orElseGet(() -> createCartForCustomer(customerId));
        }

        if (sessionId != null && !sessionId.isBlank()) {
            return cartRepository.findBySessionId(sessionId)
                    .map(cart -> {
                        try {
                            checkExpiration(cart);
                            return cart;
                        } catch (EntityNotFoundException e) {
                            // If expired and deleted, create new guest cart with NEW ID?
                            // User said "extinguir carrito... eliminar registros... incluye ID en frontend"
                            // If we return a new cart here, frontend might keep old ID?
                            // But method signature receives sessionId.
                            // If we create new guest cart, we usually generate new ID.
                            // But if we return new cart object, frontend receives it.
                            // For simplicity, we create new cart with NEW ID.
                            return createGuestCart(generateSessionId());
                        }
                    })
                    .orElseGet(() -> createGuestCart(sessionId)); // If not found, use provided ID? Or generate?
            // If provided ID is not found, we create new cart with THAT ID.
        }

        return createGuestCart(generateSessionId());
    }

    private void checkExpiration(Cart cart) {
        // Expiration logic: 24 hours from last update
        // Applies to both Guest and Customer carts?
        // User said "ya este o no logeado".
        // So we apply to all.
        java.time.LocalDateTime expirationTime = cart.getUpdatedAt().plusHours(24);
        if (java.time.LocalDateTime.now().isAfter(expirationTime)) {
            cartRepository.delete(cart);
            throw new EntityNotFoundException("El carrito ha expirado y ha sido eliminado");
        }
    }

    private Cart createGuestCart(String sessionId) {
        return Cart.builder()
                .sessionId(sessionId)
                .type(CartType.GUEST)
                .build();
    }

    private Cart createCartForCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe el cliente con ID: " + customerId));

        CartType cartType = cartTypeResolver.resolveCartType(customer);

        return Cart.builder()
                .customer(customer)
                .type(cartType)
                .build();
    }

    private String generateSessionId() {
        return UuidUtil.generateUniqueId(cartRepository::existsBySessionId);
    }
}
