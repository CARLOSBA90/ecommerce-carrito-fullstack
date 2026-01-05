package com.ecommerce.carrito.config.seeder;

import com.ecommerce.carrito.model.PromoDate;
import com.ecommerce.carrito.repository.PromoDateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromoDateSeeder implements EntitySeeder {

    private final PromoDateRepository promoDateRepository;

    @Override
    public void seed() {
        log.info("Seeding promo dates...");

        List<PromoDate> promoDates = new ArrayList<>();

        promoDates.add(createPromoDate(2026, 1, 4, "TEST PROMO ELECTRONICA"));
        promoDates.add(createPromoDate(2026, 11, 27, "Black Friday 2026"));
        promoDates.add(createPromoDate(2026, 11, 30, "Cyber Monday 2026"));
        promoDates.add(createPromoDate(2027, 1, 1, "Año Nuevo 2027"));

        promoDateRepository.saveAll(promoDates);
        log.info("Seeded {} promo dates", promoDateRepository.count());
    }

    private PromoDate createPromoDate(int year, int month, int day, String description) {
        PromoDate promo = new PromoDate();
        promo.setDate(LocalDate.of(year, month, day));
        promo.setDescription(description);
        return promo;
    }

    @Override
    public boolean shouldSeed() {
        return promoDateRepository.count() == 0;
    }
}
