package com.ecommerce.carrito.config;

import com.ecommerce.carrito.config.seeder.CompositeSeeder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Runner class that executes database seeding on application startup.
 * Uses CommandLineRunner to run after the application context is loaded.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeederRunner implements CommandLineRunner {

    private final CompositeSeeder compositeSeeder;

    @Override
    public void run(String... args) {
        log.info("Checking if database seeding is needed...");

        if (compositeSeeder.shouldSeed()) {
            compositeSeeder.seed();
        } else {
            log.info("Database already populated, skipping seeding.");
        }
    }
}
