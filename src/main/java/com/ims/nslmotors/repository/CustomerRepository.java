package com.ims.nslmotors.repository;

import com.ims.nslmotors.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    // Email ile müşteri bul
    Optional<Customer> findByEmail(String email);
}
