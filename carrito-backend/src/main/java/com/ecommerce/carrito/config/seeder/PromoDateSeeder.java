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

        PromoDate blackFriday = new PromoDate();
        blackFriday.setDate(LocalDate.of(2026, 11, 27));
        blackFriday.setDescription("Black Friday 2026");
        promoDates.add(blackFriday);

        PromoDate cyberMonday = new PromoDate();
        cyberMonday.setDate(LocalDate.of(2026, 11, 30));
        cyberMonday.setDescription("Cyber Monday 2026");
        promoDates.add(cyberMonday);

        PromoDate newYear = new PromoDate();
        newYear.setDate(LocalDate.of(2027, 1, 1));
        newYear.setDescription("Año Nuevo 2027");
        promoDates.add(newYear);

        promoDateRepository.saveAll(promoDates);
        log.info("Seeded {} promo dates", promoDateRepository.count());
    }

    @Override
    public boolean shouldSeed() {
        return promoDateRepository.count() == 0;
    }
}
