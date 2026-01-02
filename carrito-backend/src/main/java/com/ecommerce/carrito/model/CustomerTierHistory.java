package com.ecommerce.carrito.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.ecommerce.carrito.model.enums.CustomerTier;

@Entity
@Table(name = "customer_tier_history")
@Getter
@Setter
public class CustomerTierHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private CustomerTier oldTier;

    @Enumerated(EnumType.STRING)
    private CustomerTier newTier;

    private LocalDateTime changeDate;
}