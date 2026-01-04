package com.ecommerce.carrito.repository;

import com.ecommerce.carrito.model.CustomerTierHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerTierHistoryRepository extends JpaRepository<CustomerTierHistory, Long> {
    List<CustomerTierHistory> findByCustomer_Id(Long customerId);
}
