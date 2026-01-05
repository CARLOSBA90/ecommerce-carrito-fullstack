package com.ecommerce.carrito.service;

import com.ecommerce.carrito.model.Customer;
import com.ecommerce.carrito.model.CustomerTierHistory;
import com.ecommerce.carrito.model.enums.CustomerTier;
import com.ecommerce.carrito.repository.CustomerRepository;
import com.ecommerce.carrito.repository.CustomerTierHistoryRepository;
import com.ecommerce.carrito.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TierEvaluationService {

    private static final BigDecimal VIP_THRESHOLD = new BigDecimal("10000");

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final CustomerTierHistoryRepository tierHistoryRepository;

    /**
     * @return Number of customers whose tier was updated
     */
    @Transactional
    public int evaluateAllCustomerTiers() {
        log.info("Starting tier evaluation for all customers");

        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        LocalDateTime startDate = previousMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = previousMonth.atEndOfMonth().atTime(23, 59, 59);

        log.info("Evaluating based on period: {} to {}", startDate, endDate);

        List<Customer> allCustomers = customerRepository.findAll();
        int updatedCount = 0;

        System.out.println("=".repeat(80));
        System.out.println("TIER EVALUATION REPORT - " + java.time.LocalDateTime.now());
        System.out.println("Period: " + startDate + " to " + endDate);
        System.out.println("=".repeat(80));

        for (Customer customer : allCustomers) {
            if (evaluateCustomerTier(customer, startDate, endDate)) {
                updatedCount++;
                System.out.println(String.format(
                        "✓ Customer #%d (%s) - Tier updated to: %s",
                        customer.getId(),
                        customer.getFullName(),
                        customer.getTier()));
            }
        }

        System.out.println("=".repeat(80));
        if (updatedCount == 0) {
            System.out.println("NO TIER CHANGES - All customers maintain their current tier");
        } else {
            System.out.println(String.format(
                    "TIER CHANGES: %d customers updated out of %d total customers",
                    updatedCount,
                    allCustomers.size()));
        }
        System.out.println("=".repeat(80));

        log.info("Tier evaluation completed. {} customers updated out of {}", updatedCount, allCustomers.size());
        return updatedCount;
    }

    /**
     * @return true if tier was updated, false otherwise
     */
    @Transactional
    public boolean evaluateCustomerTier(Customer customer, LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal monthlySpending = calculateMonthlySpending(customer, startDate, endDate);
        CustomerTier currentTier = customer.getTier();
        CustomerTier newTier = currentTier;

        if (!CustomerTier.VIP.equals(currentTier) && shouldUpgradeToVip(monthlySpending)) {
            newTier = CustomerTier.VIP;
            log.info("Customer {} upgraded to VIP (spent: {})", customer.getId(), monthlySpending);
        } else if (CustomerTier.VIP.equals(currentTier) && shouldDowngradeFromVip(customer, startDate, endDate)) {
            newTier = CustomerTier.COMMON;
            log.info("Customer {} downgraded to COMMON (no purchases in period)", customer.getId());
        }

        if (!currentTier.equals(newTier)) {
            updateTier(customer, currentTier, newTier);
            return true;
        }

        return false;
    }

    /**
     * Calculates total spending (after discounts) for a customer in a given period
     */
    public BigDecimal calculateMonthlySpending(Customer customer, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.sumTotalAmountByCustomerAndDateBetween(
                customer.getId(),
                startDate,
                endDate);
    }

    /**
     * Determines if spending qualifies for VIP upgrade
     */
    public boolean shouldUpgradeToVip(BigDecimal spending) {
        return spending.compareTo(VIP_THRESHOLD) > 0;
    }

    /**
     * Determines if customer should be downgraded from VIP (no purchases in period)
     */
    public boolean shouldDowngradeFromVip(Customer customer, LocalDateTime startDate, LocalDateTime endDate) {
        int orderCount = orderRepository.countByCustomer_IdAndGeneratedAtBetween(
                customer.getId(),
                startDate,
                endDate);
        return orderCount == 0;
    }

    /**
     * Updates customer tier and creates history record
     */
    private void updateTier(Customer customer, CustomerTier oldTier, CustomerTier newTier) {
        customer.setTier(newTier);
        customerRepository.save(customer);

        CustomerTierHistory history = new CustomerTierHistory();
        history.setCustomer(customer);
        history.setOldTier(oldTier);
        history.setNewTier(newTier);
        history.setChangeDate(LocalDateTime.now());

        tierHistoryRepository.save(history);
    }
}
