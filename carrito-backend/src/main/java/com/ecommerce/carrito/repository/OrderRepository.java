package com.ecommerce.carrito.repository;

import com.ecommerce.carrito.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer_Id(Long customerId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.customer.id = :customerId AND o.generatedAt >= :startDate")
    BigDecimal sumTotalAmountByCustomerAndDateAfter(@Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.customer.id = :customerId AND o.generatedAt BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalAmountByCustomerAndDateBetween(@Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    int countByCustomer_IdAndGeneratedAtBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
}
