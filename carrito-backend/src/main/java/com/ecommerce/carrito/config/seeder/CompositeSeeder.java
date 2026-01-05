package com.ecommerce.carrito.config.seeder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CompositeSeeder implements EntitySeeder {

    private final List<EntitySeeder> seeders = new ArrayList<>();

    public CompositeSeeder(
            UserSeeder userSeeder,
            CustomerSeeder customerSeeder,
            ProductSeeder productSeeder,
            PromoDateSeeder promoDateSeeder,
            DiscountSeeder discountSeeder) {
        seeders.add(userSeeder);
        seeders.add(customerSeeder);
        seeders.add(productSeeder);
        seeders.add(promoDateSeeder);
        seeders.add(discountSeeder);
    }

    @Override
    public void seed() {
        log.info("=== Starting database seeding ===");

        for (EntitySeeder seeder : seeders) {
            if (seeder.shouldSeed()) {
                log.info("Running seeder: {}", seeder.getName());
                seeder.seed();
            } else {
                log.info("Skipping seeder: {} (data already exists)", seeder.getName());
            }
        }

        log.info("=== Database seeding completed ===");
    }

    @Override
    public boolean shouldSeed() {
        // Seed if any of the child seeders should seed
        return seeders.stream().anyMatch(EntitySeeder::shouldSeed);
    }
}
