package com.ecommerce.carrito.controller;

import com.ecommerce.carrito.dto.cart.AddItemToCartRequestDto;
import com.ecommerce.carrito.dto.cart.AssignCartToUserRequestDto;
import com.ecommerce.carrito.dto.cart.CartResponseDto;
import com.ecommerce.carrito.service.ICartService;
import com.ecommerce.carrito.service.IOrderService;
import com.ecommerce.carrito.repository.CustomerRepository;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {

    private final ICartService cartService;
    private final IOrderService orderService;
    private final CustomerRepository customerRepository;

    @GetMapping("/generate-session")
    public ResponseEntity<String> generateSessionId() {
        return ResponseEntity.ok(UUID.randomUUID().toString());
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<CartResponseDto> getCartBySession(@PathVariable String sessionId) {
        return ResponseEntity.ok(cartService.getCartBySessionId(sessionId));
    }

    @GetMapping("/me")
    public ResponseEntity<CartResponseDto> getMyCart(Authentication authentication) {
        Long customerId = getCustomerIdFromAuth(authentication);
        if (customerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(cartService.getCartByCustomerId(customerId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addItemToCart(
            @Valid @RequestBody AddItemToCartRequestDto request) {
        CartResponseDto cart = cartService.addItemToCart(request);
        return ResponseEntity.status(HttpStatus.OK).body(cart);
    }

    @PostMapping("/assign")
    public ResponseEntity<CartResponseDto> assignCartToUser(
            @Valid @RequestBody AssignCartToUserRequestDto request) {
        return ResponseEntity.ok(cartService.assignCartToUser(request));
    }

    @DeleteMapping("/{sessionId}/items/{productId}")
    public ResponseEntity<CartResponseDto> removeItemFromCart(
            @PathVariable String sessionId,
            @PathVariable Long productId,
            Authentication authentication) {
        Long customerId = getCustomerIdFromAuth(authentication);
        return ResponseEntity.ok(cartService.removeItemFromCart(sessionId, customerId, productId));
    }

    @PutMapping("/{sessionId}/items/{productId}/quantity/{quantity}")
    public ResponseEntity<CartResponseDto> updateItemQuantity(
            @PathVariable String sessionId,
            @PathVariable Long productId,
            @PathVariable Integer quantity,
            Authentication authentication) {
        Long customerId = getCustomerIdFromAuth(authentication);
        return ResponseEntity.ok(cartService.updateItemQuantity(sessionId, customerId, productId, quantity));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestParam String sessionId, Authentication authentication) {
        Long customerId = authentication != null ? getCustomerIdFromAuth(authentication) : null;
        cartService.clearCart(sessionId, customerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<com.ecommerce.carrito.dto.order.OrderResponseDto> confirmCart(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        Long customerId = getCustomerIdFromAuth(authentication);
        if (customerId == null) {
            return ResponseEntity.status(401).build();
        }
        com.ecommerce.carrito.dto.order.OrderResponseDto order = orderService.createOrder(customerId);
        return ResponseEntity.ok(order);
    }

    private Long getCustomerIdFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            return customerRepository.findByUser_Username(username)
                    .map(com.ecommerce.carrito.model.Customer::getId)
                    .orElse(null);
        }
        return null;
    }
}
