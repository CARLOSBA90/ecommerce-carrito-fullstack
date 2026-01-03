package com.ecommerce.carrito.repository;

import com.ecommerce.carrito.model.PromoDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PromoDateRepository extends JpaRepository<PromoDate, Long> {
    Optional<PromoDate> findByDate(LocalDate date);

    boolean existsByDate(LocalDate date);
}
