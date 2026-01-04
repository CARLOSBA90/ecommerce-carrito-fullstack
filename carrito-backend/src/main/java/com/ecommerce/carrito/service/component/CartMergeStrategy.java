package com.ecommerce.carrito.service.component;

import com.ecommerce.carrito.model.Cart;
import com.ecommerce.carrito.model.CartItem;
import com.ecommerce.carrito.model.Customer;
import com.ecommerce.carrito.repository.CartItemRepository;
import com.ecommerce.carrito.repository.CartRepository;
import com.ecommerce.carrito.resolver.CartTypeResolver;
import com.ecommerce.carrito.validator.StockValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CartMergeStrategy {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final StockValidator stockValidator;
    private final CartTypeResolver cartTypeResolver;
    private final CartItemManager cartItemManager;

    @Transactional
    public void assignGuestCartToCustomer(Cart guestCart, Customer customer) {
        guestCart.setCustomer(customer);
        guestCart.setSessionId(null);
        guestCart.setType(cartTypeResolver.resolveCartType(customer));
        cartRepository.save(guestCart);
    }

    @Transactional
    public void mergeCartsAndDeleteGuest(Cart existingCart, Cart guestCart) {
        for (CartItem guestItem : guestCart.getItems()) {
            CartItem existingItem = cartItemManager.findOrCreateCartItem(existingCart, guestItem.getProduct());
            int newQuantity = existingItem.getQuantity() + guestItem.getQuantity();

            stockValidator.validateStock(guestItem.getProduct(), newQuantity);

            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        }

        cartRepository.delete(guestCart);
        cartRepository.save(existingCart);
    }
}
