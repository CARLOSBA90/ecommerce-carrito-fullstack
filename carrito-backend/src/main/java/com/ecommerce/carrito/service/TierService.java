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

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        BigDecimal totalSpent = orderRepository.sumTotalAmountByCustomerAndDateAfter(customer.getId(), thirtyDaysAgo);

        log.info("Customer {} spent {} in last 30 days", customer.getId(), totalSpent);

        if (totalSpent.compareTo(VIP_THRESHOLD) >= 0) {
            upgradeToVip(customer);
        } else {
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

        tierHistoryRepository.save(history);
    }

    public java.util.List<com.ecommerce.carrito.soap.CustomerReportItem> getCustomerReport(
            com.ecommerce.carrito.soap.ReportType type, int month, int year) {

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);

        java.util.List<com.ecommerce.carrito.soap.CustomerReportItem> report = new java.util.ArrayList<>();

        if (com.ecommerce.carrito.soap.ReportType.CURRENT_VIP.equals(type)) {
            java.util.List<Customer> vips = customerRepository.findByTier(CustomerTier.VIP);
            for (Customer c : vips) {
                report.add(createReportItem(c, null, start, end));
            }
        } else {
            java.util.List<CustomerTierHistory> changes = tierHistoryRepository.findByChangeDateBetween(start, end);

            for (CustomerTierHistory h : changes) {
                boolean include = false;
                if (com.ecommerce.carrito.soap.ReportType.ALL_CHANGES.equals(type)) {
                    include = true;
                } else if (com.ecommerce.carrito.soap.ReportType.NEW_VIP.equals(type)) {
                    include = !CustomerTier.VIP.equals(h.getOldTier()) && CustomerTier.VIP.equals(h.getNewTier());
                } else if (com.ecommerce.carrito.soap.ReportType.LOST_VIP.equals(type)) {
                    include = CustomerTier.VIP.equals(h.getOldTier()) && !CustomerTier.VIP.equals(h.getNewTier());
                }

                if (include) {
                    report.add(createReportItem(h.getCustomer(), h, start, end));
                }
            }
        }

        return report;
    }

    private com.ecommerce.carrito.soap.CustomerReportItem createReportItem(
            Customer customer,
            CustomerTierHistory history,
            LocalDateTime start,
            LocalDateTime end) {

        com.ecommerce.carrito.soap.CustomerReportItem item = new com.ecommerce.carrito.soap.CustomerReportItem();
        item.setCustomerId(customer.getId());
        item.setFullName(customer.getFullName());

        if (history != null) {
            item.setTierFrom(history.getOldTier().name());
            item.setTierTo(history.getNewTier().name());
            item.setDateOfChange(history.getChangeDate().toString());
        } else {
            item.setTierTo(customer.getTier().name());
        }

        BigDecimal spent = orderRepository.sumTotalAmountByCustomerAndDateBetween(customer.getId(), start, end);
        int count = orderRepository.countByCustomer_IdAndGeneratedAtBetween(customer.getId(), start, end);

        item.setTotalSpent(spent);
        item.setTotalOrders(count);

        return item;
    }
}
