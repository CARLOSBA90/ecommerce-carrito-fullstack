package com.ecommerce.carrito.service.impl;

import com.ecommerce.carrito.dto.cart.AddItemToCartRequestDto;
import com.ecommerce.carrito.dto.cart.AssignCartToUserRequestDto;
import com.ecommerce.carrito.dto.cart.CartResponseDto;
import com.ecommerce.carrito.exception.EntityNotFoundException;
import com.ecommerce.carrito.mapper.CartMapper;
import com.ecommerce.carrito.model.Cart;
import com.ecommerce.carrito.model.CartItem;
import com.ecommerce.carrito.model.Customer;
import com.ecommerce.carrito.model.Product;
import com.ecommerce.carrito.model.enums.CartType;
import com.ecommerce.carrito.repository.CartItemRepository;
import com.ecommerce.carrito.repository.CartRepository;
import com.ecommerce.carrito.repository.CustomerRepository;
import com.ecommerce.carrito.resolver.CartTypeResolver;
import com.ecommerce.carrito.service.ICartService;
import com.ecommerce.carrito.service.IProductService;
import com.ecommerce.carrito.util.UuidUtil;
import com.ecommerce.carrito.util.ValidationUtil;
import com.ecommerce.carrito.validator.StockValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final IProductService productService;

    // Helper components
    private final CartMapper cartMapper;
    private final CartTypeResolver cartTypeResolver;
    private final StockValidator stockValidator;

    @Override
    public CartResponseDto getCartBySessionId(String sessionId) {
        ValidationUtil.validateSessionId(sessionId);

        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe un carrito con sessionId: " + sessionId));

        return cartMapper.toResponseDto(cart);
    }

    @Override
    public CartResponseDto getCartByCustomerId(Long customerId) {
        ValidationUtil.validateId(customerId, "Customer");

        Cart cart = cartRepository.findByCustomer_Id(customerId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe un carrito para el cliente con ID: " + customerId));

        return cartMapper.toResponseDto(cart);
    }

    @Override
    @Transactional
    public CartResponseDto addItemToCart(AddItemToCartRequestDto request) {
        Cart cart = getOrCreateCart(request.getSessionId(), request.getCustomerId());

        Product product = productService.findProductById(request.getProductId());
        int requestedQuantity = request.getQuantity() != null ? request.getQuantity() : 1;

        stockValidator.validateStock(product, requestedQuantity);

        CartItem cartItem = findOrCreateCartItem(cart, product);
        int newQuantity = cartItem.getQuantity() + requestedQuantity;

        stockValidator.validateStock(product, newQuantity);
        cartItem.setQuantity(newQuantity);

        cartItemRepository.save(cartItem);
        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toResponseDto(savedCart);
    }

    @Override
    @Transactional
    public CartResponseDto assignCartToUser(AssignCartToUserRequestDto request) {
        ValidationUtil.validateSessionId(request.getSessionId());
        ValidationUtil.validateId(request.getCustomerId(), "Customer");

        Cart guestCart = cartRepository.findBySessionId(request.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe un carrito con sessionId: " + request.getSessionId()));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe el cliente con ID: " + request.getCustomerId()));

        cartRepository.findByCustomer_Id(customer.getId())
                .ifPresentOrElse(
                        existingCart -> mergeCartsAndDeleteGuest(existingCart, guestCart),
                        () -> assignGuestCartToCustomer(guestCart, customer));

        Cart updatedCart = cartRepository.findByCustomer_Id(customer.getId())
                .orElseThrow(() -> new EntityNotFoundException("Error al asignar carrito al usuario"));

        return cartMapper.toResponseDto(updatedCart);
    }

    @Override
    @Transactional
    public CartResponseDto removeItemFromCart(String sessionId, Long productId) {
        ValidationUtil.validateSessionId(sessionId);

        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe un carrito con sessionId: " + sessionId));

        CartItem cartItem = cartItemRepository.findByCartAndProduct_Id(cart, productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El producto no existe en el carrito"));

        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toResponseDto(savedCart);
    }

    @Override
    @Transactional
    public CartResponseDto updateItemQuantity(String sessionId, Long productId, Integer quantity) {
        ValidationUtil.validateSessionId(sessionId);
        ValidationUtil.validatePositiveQuantity(quantity);

        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe un carrito con sessionId: " + sessionId));

        CartItem cartItem = cartItemRepository.findByCartAndProduct_Id(cart, productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El producto no existe en el carrito"));

        stockValidator.validateStock(cartItem.getProduct(), quantity);
        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);
        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toResponseDto(savedCart);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private Cart getOrCreateCart(String sessionId, Long customerId) {
        if (customerId != null) {
            return cartRepository.findByCustomer_Id(customerId)
                    .orElseGet(() -> createCartForCustomer(customerId));
        }

        if (sessionId != null && !sessionId.isBlank()) {
            return cartRepository.findBySessionId(sessionId)
                    .orElseGet(() -> createGuestCart(sessionId));
        }

        return createGuestCart(generateSessionId());
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

    private CartItem findOrCreateCartItem(Cart cart, Product product) {
        return cartItemRepository.findByCartAndProduct_Id(cart, product.getId())
                .orElseGet(() -> {
                    CartItem newItem = CartItem.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(0)
                            .build();
                    cart.addItem(newItem);
                    return newItem;
                });
    }

    private void assignGuestCartToCustomer(Cart guestCart, Customer customer) {
        guestCart.setCustomer(customer);
        guestCart.setSessionId(null);
        guestCart.setType(cartTypeResolver.resolveCartType(customer));
        cartRepository.save(guestCart);
    }

    private void mergeCartsAndDeleteGuest(Cart existingCart, Cart guestCart) {
        for (CartItem guestItem : guestCart.getItems()) {
            CartItem existingItem = findOrCreateCartItem(existingCart, guestItem.getProduct());
            int newQuantity = existingItem.getQuantity() + guestItem.getQuantity();
            stockValidator.validateStock(guestItem.getProduct(), newQuantity);
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        }

        cartRepository.delete(guestCart);
        cartRepository.save(existingCart);
    }
}
