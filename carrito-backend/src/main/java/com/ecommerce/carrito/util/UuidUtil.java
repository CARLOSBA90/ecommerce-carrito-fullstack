package com.ecommerce.carrito.util;

import java.util.UUID;
import java.util.function.Predicate;

public final class UuidUtil {

    private UuidUtil() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * @return a randomly generated UUID string
     */
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    /**
     * @param existsChecker a predicate that returns true if the UUID already exists
     * @return a unique UUID string that doesn't exist according to the checker
     */
    public static String generateUniqueId(Predicate<String> existsChecker) {
        String uuid;
        do {
            uuid = generateUniqueId();
        } while (existsChecker.test(uuid));
        return uuid;
    }
}
