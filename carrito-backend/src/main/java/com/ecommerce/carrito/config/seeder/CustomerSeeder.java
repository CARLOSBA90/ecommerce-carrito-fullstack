package com.ecommerce.carrito.config.seeder;

import com.ecommerce.carrito.model.Customer;
import com.ecommerce.carrito.model.CustomerTierHistory;
import com.ecommerce.carrito.model.User;
import com.ecommerce.carrito.model.enums.CustomerTier;
import com.ecommerce.carrito.repository.CustomerRepository;
import com.ecommerce.carrito.repository.UserRepository;
import jakarta.persistence.EntityManager;
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
    private final EntityManager entityManager;

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

            // Half of customers are VIP, half are COMMON
            CustomerTier tier = (i < customerNames.length / 2) ? CustomerTier.VIP : CustomerTier.COMMON;
            customer.setTier(tier);
            customer.setUser(customerUsers.get(i));

            Customer savedCustomer = customerRepository.save(customer);

            // Generate random tier history for some customers (30% chance)
            if (Math.random() < 0.3) {
                generateTierHistory(savedCustomer);
            }
        }

        log.info("Seeded {} customers", customerRepository.count());
    }

    private void generateTierHistory(Customer customer) {
        // Generate 1-3 random tier changes
        int historyCount = (int) (Math.random() * 3) + 1;

        for (int i = 0; i < historyCount; i++) {
            CustomerTierHistory history = new CustomerTierHistory();
            history.setCustomer(customer);

            // Random tier change (COMMON <-> VIP)
            if (i == 0) {
                // First entry: opposite of current tier
                history.setOldTier(customer.getTier() == CustomerTier.VIP ? CustomerTier.COMMON : CustomerTier.VIP);
                history.setNewTier(customer.getTier());
            } else {
                // Subsequent entries: toggle between tiers
                CustomerTier previous = customer.getTier() == CustomerTier.VIP ? CustomerTier.COMMON : CustomerTier.VIP;
                history.setOldTier(previous);
                history.setNewTier(customer.getTier() == CustomerTier.VIP ? CustomerTier.COMMON : CustomerTier.VIP);
            }

            // Random date within last 365 days
            int daysAgo = (int) (Math.random() * 365);
            history.setChangeDate(java.time.LocalDateTime.now().minusDays(daysAgo));

            // Persist using EntityManager
            entityManager.persist(history);
        }
    }

    @Override
    public boolean shouldSeed() {
        return customerRepository.count() == 0;
    }
}
