package com.ecommerce.carrito.config.seeder;

/**
 * Component interface for the Composite pattern.
 * Defines the contract for all seeders.
 */
public interface EntitySeeder {

    /**
     * Execute the seeding logic for this entity
     */
    void seed();

    /**
     * Check if seeding should be performed (typically if table is empty)
     * 
     * @return true if seeding is needed, false otherwise
     */
    boolean shouldSeed();

    /**
     * Get the name of this seeder for logging purposes
     * 
     * @return seeder name
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }
}
