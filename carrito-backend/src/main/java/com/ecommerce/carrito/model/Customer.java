package com.ecommerce.carrito.model;

import jakarta.persistence.*;
import lombok.*;

import com.ecommerce.carrito.model.enums.CustomerTier;

import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;

    @Enumerated(EnumType.STRING)
    private CustomerTier tier;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<CustomerTierHistory> history;
}