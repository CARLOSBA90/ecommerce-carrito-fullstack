package com.ecommerce.carrito.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private Long productId;
    private String productDescription; // snapshot

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountApplied;

    @Column(length = 50)
    private String itemPromoCode;

    private int quantity;

    public BigDecimal getFinalLinePrice() {
        BigDecimal finalUnit = unitPrice.subtract(discountApplied);
        return finalUnit.multiply(BigDecimal.valueOf(quantity));
    }
}