package com.ecommerce.carrito.service;

import com.ecommerce.carrito.event.OrderCreatedEvent;
import com.ecommerce.carrito.model.Customer;
import com.ecommerce.carrito.model.CustomerTierHistory;
import com.ecommerce.carrito.model.Order;
import com.ecommerce.carrito.model.enums.CustomerTier;
import com.ecommerce.carrito.repository.CustomerRepository;
import com.ecommerce.carrito.repository.CustomerTierHistoryRepository;
import com.ecommerce.carrito.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TierService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final CustomerTierHistoryRepository tierHistoryRepository;

    private static final BigDecimal VIP_THRESHOLD = new BigDecimal("10000.00");

    @EventListener
    @Transactional
    public void onOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();
        Customer customer = order.getCustomer();

        log.info("Checking tier update for customer: {}", customer.getId());

        // Calculate total spent in last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        BigDecimal totalSpent = orderRepository.sumTotalAmountByCustomerAndDateAfter(customer.getId(), thirtyDaysAgo);

        log.info("Customer {} spent {} in last 30 days", customer.getId(), totalSpent);

        if (totalSpent.compareTo(VIP_THRESHOLD) >= 0) {
            upgradeToVip(customer);
        } else {
            // Logic to downgrade? User only mentioned "actualizar TIER, si hay cambios se
            // debe agregar registro".
            // Does downgrade happen automatically?
            // Usually upgrades are sticky or recalculated periodically.
            // If they are currently VIP but spent < 10k in last 30 days window (rolling),
            // maybe they should downgrade?
            // But if they just bought something, they are MORE likely to be VIP.
            // If they were COMMON and now > 10k -> Upgrade.
            // If they were VIP and now < 10k?
            // Let's implement Upgrade to VIP.
            // Downgrade logic usually complex (grace period). We'll assume Upgrade only or
            // Toggle based on 30 day window strict?
            // If strict: if < 10k and is VIP -> Downgrade.
            // Let's implement strict check based on "actualizar TIER".
            if (CustomerTier.VIP.equals(customer.getTier())) {
                checkDowngrade(customer, totalSpent);
            }
        }
    }

    private void upgradeToVip(Customer customer) {
        if (!CustomerTier.VIP.equals(customer.getTier())) {
            log.info("Upgrading customer {} to VIP", customer.getId());
            updateTier(customer, CustomerTier.COMMON, CustomerTier.VIP);
        }
    }

    private void checkDowngrade(Customer customer, BigDecimal totalSpent) {
        // If total spent < 10k, downgrade to COMMON
        if (totalSpent.compareTo(VIP_THRESHOLD) < 0) {
            log.info("Downgrading customer {} to COMMON (Total spent: {})", customer.getId(), totalSpent);
            updateTier(customer, CustomerTier.VIP, CustomerTier.COMMON);
        }
    }

    private void updateTier(Customer customer, CustomerTier oldTier, CustomerTier newTier) {
        customer.setTier(newTier);
        customerRepository.save(customer);

        CustomerTierHistory history = new CustomerTierHistory();
        history.setCustomer(customer);
        history.setOldTier(oldTier);
        history.setNewTier(newTier);
        history.setChangeDate(LocalDateTime.now());
        // history.setChangeReason(...) if field exists

        tierHistoryRepository.save(history);
    }
}
