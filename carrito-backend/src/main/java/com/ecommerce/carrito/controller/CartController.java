package com.ecommerce.carrito.controller;

import com.ecommerce.carrito.dto.cart.AddItemToCartRequestDto;
import com.ecommerce.carrito.dto.cart.AssignCartToUserRequestDto;
import com.ecommerce.carrito.dto.cart.CartResponseDto;
import com.ecommerce.carrito.service.ICartService;
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

    @GetMapping("/generate-session")
    public ResponseEntity<String> generateSessionId() {
        return ResponseEntity.ok(UUID.randomUUID().toString());
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<CartResponseDto> getCartBySession(@PathVariable String sessionId) {
        return ResponseEntity.ok(cartService.getCartBySessionId(sessionId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CartResponseDto> getCartByCustomer(@PathVariable Long customerId) {
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
            @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(sessionId, productId));
    }

    @PutMapping("/{sessionId}/items/{productId}/quantity/{quantity}")
    public ResponseEntity<CartResponseDto> updateItemQuantity(
            @PathVariable String sessionId,
            @PathVariable Long productId,
            @PathVariable Integer quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(sessionId, productId, quantity));
    }

    @PostMapping("/confirm")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> confirmCart(
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {

        String message = "Carrito confirmado para usuario: " + userDetails.getUsername();
        return ResponseEntity.ok(message);
    }
}
