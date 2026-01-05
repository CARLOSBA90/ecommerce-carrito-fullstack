package com.ecommerce.carrito.repository;

import com.ecommerce.carrito.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    java.util.Optional<Customer> findByUser_Username(String username);

    java.util.List<Customer> findByTier(com.ecommerce.carrito.model.enums.CustomerTier tier);
}
