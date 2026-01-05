package com.ecommerce.carrito.service;

import com.ecommerce.carrito.model.Cart;
import com.ecommerce.carrito.model.CartItem;
import com.ecommerce.carrito.model.Discount;
import com.ecommerce.carrito.model.Product;
import com.ecommerce.carrito.model.enums.CartType;
import com.ecommerce.carrito.model.enums.CartTypeFilter;
import com.ecommerce.carrito.model.enums.DiscountType;
import com.ecommerce.carrito.repository.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountService discountService;

    private Cart cart;
    private List<Discount> discounts;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setType(CartType.COMMON);
        cart.setItems(new ArrayList<>());
        discounts = new ArrayList<>();
    }

    @Test
    void testCalculateTotalDiscount_With4Items_ShouldApply25Percent() {
        // Arrange: Create cart with 4 items
        addProductToCart(new BigDecimal("100"), 4);

        // Create 25% discount for 4+ items
        Discount discount25Percent = Discount.builder()
                .code("DESC_4_PLUS")
                .name("25% por 4+ productos")
                .discountType(DiscountType.PERCENTAGE)
                .value(new BigDecimal("25.00"))
                .cartTypeApplies(CartTypeFilter.ANY)
                .conditionType("MIN_QUANTITY")
                .conditionValue(4)
                .priority(10)
                .active(true)
                .build();

        discounts.add(discount25Percent);
        when(discountRepository.findApplicableDiscounts(any())).thenReturn(discounts);

        // Act
        List<Discount> applicableDiscounts = discountService.findApplicableDiscounts(cart);
        BigDecimal totalDiscount = discountService.calculateTotalDiscount(cart, applicableDiscounts);

        // Assert
        assertEquals(1, applicableDiscounts.size());
        assertEquals(new BigDecimal("100.00"), totalDiscount); // 25% of 400 = 100
    }

    @Test
    void testCalculateTotalDiscount_WhenDiscountsExceedSubtotal_ShouldCapAtSubtotal() {
        // Arrange: Create cart with low subtotal
        addProductToCart(new BigDecimal("30"), 10);

        // Create large discount that exceeds subtotal
        Discount fixedDiscount = Discount.builder()
                .code("DESC_LARGE")
                .name("$500 descuento")
                .discountType(DiscountType.FIXED)
                .value(new BigDecimal("500.00"))
                .cartTypeApplies(CartTypeFilter.ANY)
                .conditionType("MIN_QUANTITY")
                .conditionValue(10)
                .priority(20)
                .active(true)
                .build();

        discounts.add(fixedDiscount);
        when(discountRepository.findApplicableDiscounts(any())).thenReturn(discounts);

        // Act
        List<Discount> applicableDiscounts = discountService.findApplicableDiscounts(cart);
        BigDecimal totalDiscount = discountService.calculateTotalDiscount(cart, applicableDiscounts);

        // Assert: Discount should be capped at subtotal
        BigDecimal subtotal = cart.getSubtotal();
        assertEquals(subtotal, totalDiscount); // Should not exceed subtotal of 300
        assertEquals(new BigDecimal("300"), totalDiscount);
    }

    private void addProductToCart(BigDecimal price, int quantity) {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(price);
        product.setStock(100);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setCart(cart);

        cart.getItems().add(cartItem);
    }
}
