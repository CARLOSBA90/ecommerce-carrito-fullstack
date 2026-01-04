package com.ecommerce.carrito.config.seeder;

import com.ecommerce.carrito.model.User;
import com.ecommerce.carrito.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSeeder implements EntitySeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void seed() {
        log.info("Seeding users...");

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .enabled(true)
                .roles(Set.of("ADMIN", "CUSTOMER"))
                .build();
        userRepository.save(admin);

        for (int i = 1; i <= 8; i++) {
            User customer = User.builder()
                    .username("customer" + i)
                    .password(passwordEncoder.encode("password" + i))
                    .enabled(true)
                    .roles(Set.of("CUSTOMER"))
                    .build();
            userRepository.save(customer);
        }

        log.info("Seeded {} users", userRepository.count());
    }

    @Override
    public boolean shouldSeed() {
        return userRepository.count() == 0;
    }
}
