package com.ecommerce.carrito.model;

import com.ecommerce.carrito.model.enums.CartType; // Tu enum creado anteriormente
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @Column(unique = true)
    private String sessionId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private CartType type;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private java.time.LocalDateTime creationDate;

    @org.hibernate.annotations.UpdateTimestamp
    @Column(nullable = false)
    private java.time.LocalDateTime updatedAt;

    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cart_discounts", joinColumns = @JoinColumn(name = "cart_id"), inverseJoinColumns = @JoinColumn(name = "discount_id"))
    @Builder.Default
    private List<Discount> appliedDiscounts = new ArrayList<>();

    // Métodos de utilidad para descuentos
    public int getTotalProductCount() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public java.math.BigDecimal getSubtotal() {
        return items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public java.math.BigDecimal getTotalDiscounts() {
        // Calculate total discount from appliedDiscounts list
        // This logic depends on Discount type (fixed, percentage, etc.)
        // For now, assuming Discount has a Calculate method or similar, OR just summing
        // fixed amounts
        // If Discount logic is complex, it should be in a Service.
        // But OrderService calls Cart.getTotalDiscounts().
        // Let's assume we sum 'amount' if fixed, or calculate percentage.
        // If generic:
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        // Implementation pending full Discount logic. Returning 0 for now or summing if
        // simple.
        return total;
    }

    public java.math.BigDecimal getTotalPrice() {
        java.math.BigDecimal subtotal = getSubtotal();
        java.math.BigDecimal discounts = getTotalDiscounts();
        return subtotal.subtract(discounts).max(java.math.BigDecimal.ZERO);
    }
}