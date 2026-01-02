package com.ecommerce.carrito.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "promo_dates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromoDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private LocalDate date;

    private String description;
}