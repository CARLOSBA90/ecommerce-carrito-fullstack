package com.ecommerce.carrito.model.enums;

import lombok.Getter;

@Getter
public enum CustomerTier {
    COMMON("Común", 0),
    VIP("Usuario VIP", 10000);

    private final String description;
    private final double threshold;

    CustomerTier(String description, double threshold) {
        this.description = description;
        this.threshold = threshold;
    }
}