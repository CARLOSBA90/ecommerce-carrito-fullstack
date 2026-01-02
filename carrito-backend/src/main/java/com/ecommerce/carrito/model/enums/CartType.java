package com.ecommerce.carrito.model.enums;

import lombok.Getter;

@Getter
public enum CartType {

    COMMON("Común"),
    VIP("Promocionable VIP"),
    SPECIAL_DATE("Promocionable por Fecha Especial");

    private final String description;

    CartType(String description) {
        this.description = description;
    }
}