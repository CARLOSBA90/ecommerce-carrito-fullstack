package com.ecommerce.carrito.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código único (ej: "DESC_4_PROD", "DESC_VIP_500")
    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    // Tipo de descuento: "PERCENTAGE", "FIXED", "FREE_PRODUCT"
    @Column(nullable = false, length = 20)
    private String discountType;

    // Valor del descuento (25 para 25%, 100 para $100)
    @Column(precision = 10, scale = 2)
    private BigDecimal value;

    // Tipo de carrito al que aplica: "COMMON", "VIP", "SPECIAL_DATE", "ANY"
    @Column(nullable = false, length = 20)
    private String cartTypeApplies;

    // Condición: "EXACT_QTY_4", "MIN_QTY_10", "ALWAYS"
    @Column(name = "`condition`", length = 30)
    private String condition;

    // Activo
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    // Prioridad (menor = primero)
    @Column(nullable = false)
    @Builder.Default
    private Integer priority = 0;
}
