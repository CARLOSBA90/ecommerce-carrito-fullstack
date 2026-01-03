package com.ecommerce.carrito.config.seeder;

import com.ecommerce.carrito.model.Customer;
import com.ecommerce.carrito.model.User;
import com.ecommerce.carrito.model.enums.CustomerTier;
import com.ecommerce.carrito.repository.CustomerRepository;
import com.ecommerce.carrito.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerSeeder implements EntitySeeder {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Override
    public void seed() {
        log.info("Seeding customers...");

        // Get all customer users (excluding admin)
        List<User> customerUsers = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains("CUSTOMER") && !user.getRoles().contains("ADMIN"))
                .toList();

        String[] customerNames = {
                "Juan Pérez",
                "María García",
                "Carlos López",
                "Ana Martínez",
                "Luis Rodríguez",
                "Sofia Fernández",
                "Diego Sánchez",
                "Laura González"
        };

        for (int i = 0; i < customerUsers.size() && i < customerNames.length; i++) {
            Customer customer = new Customer();
            customer.setFullName(customerNames[i]);
            customer.setTier(CustomerTier.COMMON);
            customer.setUser(customerUsers.get(i));
            customerRepository.save(customer);
        }

        log.info("Seeded {} customers", customerRepository.count());
    }

    @Override
    public boolean shouldSeed() {
        return customerRepository.count() == 0;
    }
}
