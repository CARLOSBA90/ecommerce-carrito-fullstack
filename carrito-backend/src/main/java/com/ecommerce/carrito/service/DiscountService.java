package com.ecommerce.carrito.service;

import com.ecommerce.carrito.model.Cart;
import com.ecommerce.carrito.model.CartItem;
import com.ecommerce.carrito.model.Discount;
import com.ecommerce.carrito.model.enums.CartTypeFilter;
import com.ecommerce.carrito.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountService {

    private final DiscountRepository discountRepository;

    public List<Discount> findApplicableDiscounts(Cart cart) {
        CartTypeFilter cartTypeFilter = mapCartTypeToFilter(cart);
        List<Discount> candidates = discountRepository.findApplicableDiscounts(cartTypeFilter);

        int totalItems = cart.getTotalProductCount();
        BigDecimal subtotal = cart.getSubtotal();

        List<Discount> matched = candidates.stream()
                .filter(d -> matchesCondition(d, totalItems))
                .collect(Collectors.toList());

        return applyExclusivityRules(matched, cart, subtotal);
    }

    public BigDecimal calculateTotalDiscount(Cart cart, List<Discount> discounts) {
        BigDecimal subtotal = cart.getSubtotal();
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (Discount discount : discounts) {
            BigDecimal discountAmount = calculateDiscountAmount(cart, discount, subtotal);
            totalDiscount = totalDiscount.add(discountAmount);
            log.debug("Applied discount {}: -{}", discount.getCode(), discountAmount);
        }

        return totalDiscount.min(subtotal);
    }

    private List<Discount> applyExclusivityRules(List<Discount> discounts, Cart cart, BigDecimal subtotal) {
        List<Discount> vipDiscounts = discounts.stream()
                .filter(d -> d.getCartTypeApplies() == CartTypeFilter.VIP)
                .collect(Collectors.toList());

        List<Discount> specialDateDiscounts = discounts.stream()
                .filter(d -> d.getCartTypeApplies() == CartTypeFilter.SPECIAL_DATE)
                .collect(Collectors.toList());

        List<Discount> genericDiscounts = discounts.stream()
                .filter(d -> d.getCartTypeApplies() == CartTypeFilter.ANY ||
                        d.getCartTypeApplies() == CartTypeFilter.COMMON)
                .collect(Collectors.toList());

        if (!vipDiscounts.isEmpty() && !specialDateDiscounts.isEmpty()) {
            BigDecimal vipTotal = calculateGroupTotal(cart, vipDiscounts, subtotal);
            BigDecimal specialTotal = calculateGroupTotal(cart, specialDateDiscounts, subtotal);

            log.info("VIP discounts total: {}, Special Date discounts total: {}", vipTotal, specialTotal);

            if (vipTotal.compareTo(specialTotal) >= 0) {
                log.info("Applying VIP discounts (higher amount)");
                genericDiscounts.addAll(vipDiscounts);
            } else {
                log.info("Applying Special Date discounts (higher amount)");
                genericDiscounts.addAll(specialDateDiscounts);
            }
        } else {
            genericDiscounts.addAll(vipDiscounts);
            genericDiscounts.addAll(specialDateDiscounts);
        }

        return genericDiscounts.stream()
                .sorted(Comparator.comparing(Discount::getPriority))
                .collect(Collectors.toList());
    }

    private BigDecimal calculateGroupTotal(Cart cart, List<Discount> discounts, BigDecimal subtotal) {
        return discounts.stream()
                .map(d -> calculateDiscountAmount(cart, d, subtotal))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CartTypeFilter mapCartTypeToFilter(Cart cart) {
        if (cart.getType() == null) {
            return CartTypeFilter.COMMON;
        }

        return switch (cart.getType()) {
            case VIP -> CartTypeFilter.VIP;
            case SPECIAL_DATE -> CartTypeFilter.SPECIAL_DATE;
            default -> CartTypeFilter.COMMON;
        };
    }

    private boolean matchesCondition(Discount discount, int totalItems) {
        String conditionType = discount.getConditionType();
        Integer conditionValue = discount.getConditionValue();

        if (conditionType == null || "ALWAYS".equals(conditionType)) {
            return true;
        }

        if (conditionValue == null) {
            return false;
        }

        return switch (conditionType) {
            case "EXACT_QUANTITY" -> totalItems == conditionValue;
            case "MIN_QUANTITY" -> totalItems >= conditionValue;
            case "MAX_QUANTITY" -> totalItems < conditionValue;
            default -> false;
        };
    }

    private BigDecimal calculateDiscountAmount(Cart cart, Discount discount, BigDecimal subtotal) {
        return switch (discount.getDiscountType()) {
            case PERCENTAGE -> calculatePercentageDiscount(subtotal, discount.getValue());
            case FIXED -> calculateFixedDiscount(discount.getValue());
            case FREE_PRODUCT -> calculateFreeProductDiscount(cart);
        };
    }

    private BigDecimal calculatePercentageDiscount(BigDecimal subtotal, BigDecimal percentage) {
        return subtotal.multiply(percentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateFixedDiscount(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal calculateFreeProductDiscount(Cart cart) {
        return cart.getItems().stream()
                .map(CartItem::getProduct)
                .map(p -> p.getPrice())
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
}
