package com.ecommerce.carrito.repository;

import com.ecommerce.carrito.model.Discount;
import com.ecommerce.carrito.model.enums.CartTypeFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    List<Discount> findByCartTypeAppliesAndActiveOrderByPriorityAsc(
            CartTypeFilter cartTypeApplies,
            Boolean active);

    @Query("SELECT d FROM Discount d WHERE " +
            "d.active = true AND " +
            "(d.cartTypeApplies = :cartType OR d.cartTypeApplies = 'ANY') " +
            "ORDER BY d.priority ASC")
    List<Discount> findApplicableDiscounts(@Param("cartType") CartTypeFilter cartType);
}
