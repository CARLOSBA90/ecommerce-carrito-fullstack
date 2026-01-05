package com.ecommerce.carrito.config.seeder;

import com.ecommerce.carrito.model.Discount;
import com.ecommerce.carrito.model.enums.CartTypeFilter;
import com.ecommerce.carrito.model.enums.DiscountType;
import com.ecommerce.carrito.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscountSeeder implements EntitySeeder {

    private final DiscountRepository discountRepository;

    @Override
    public void seed() {
        log.info("Seeding discounts...");

        List<Discount> discounts = new ArrayList<>();

        discounts.addAll(createQuantityDiscounts());
        discounts.addAll(createSpecialDateDiscounts());
        discounts.addAll(createVipDiscounts());

        discountRepository.saveAll(discounts);
        log.info("Seeded {} discounts", discountRepository.count());
    }

    @Override
    public boolean shouldSeed() {
        return discountRepository.count() == 0;
    }

    private List<Discount> createQuantityDiscounts() {
        List<Discount> discounts = new ArrayList<>();

        discounts.add(Discount.builder()
                .code("DESC_4_PLUS")
                .name("25% por 4+ productos")
                .description("Descuento del 25% al comprar 4 o más productos")
                .discountType(DiscountType.PERCENTAGE)
                .value(new BigDecimal("25.00"))
                .cartTypeApplies(CartTypeFilter.ANY)
                .conditionType("MIN_QUANTITY")
                .conditionValue(4)
                .priority(10)
                .active(true)
                .build());

        discounts.add(Discount.builder()
                .code("DESC_10_PLUS")
                .name("$100 por 10+ productos")
                .description("Descuento adicional de $100 al comprar 10 o más productos")
                .discountType(DiscountType.FIXED)
                .value(new BigDecimal("100.00"))
                .cartTypeApplies(CartTypeFilter.ANY)
                .conditionType("MIN_QUANTITY")
                .conditionValue(10)
                .priority(20)
                .active(true)
                .build());

        return discounts;
    }

    private List<Discount> createSpecialDateDiscounts() {
        List<Discount> discounts = new ArrayList<>();

        discounts.add(Discount.builder()
                .code("DESC_SPECIAL_DATE")
                .name("$300 Fecha Especial")
                .description("Descuento de $300 en fechas promocionales (requiere 4+ productos)")
                .discountType(DiscountType.FIXED)
                .value(new BigDecimal("300.00"))
                .cartTypeApplies(CartTypeFilter.SPECIAL_DATE)
                .conditionType("MIN_QUANTITY")
                .conditionValue(4)
                .priority(30)
                .active(true)
                .build());

        return discounts;
    }

    private List<Discount> createVipDiscounts() {
        List<Discount> discounts = new ArrayList<>();

        discounts.add(Discount.builder()
                .code("DESC_VIP_FREE")
                .name("Producto más barato GRATIS (VIP)")
                .description("El producto más barato es gratis para clientes VIP (requiere 10+ productos)")
                .discountType(DiscountType.FREE_PRODUCT)
                .value(null)
                .cartTypeApplies(CartTypeFilter.VIP)
                .conditionType("MIN_QUANTITY")
                .conditionValue(10)
                .priority(40)
                .active(true)
                .build());

        discounts.add(Discount.builder()
                .code("DESC_VIP_500")
                .name("$500 adicional VIP")
                .description("Descuento adicional de $500 para clientes VIP (requiere 10+ productos)")
                .discountType(DiscountType.FIXED)
                .value(new BigDecimal("500.00"))
                .cartTypeApplies(CartTypeFilter.VIP)
                .conditionType("MIN_QUANTITY")
                .conditionValue(10)
                .priority(50)
                .active(true)
                .build());

        return discounts;
    }
}
