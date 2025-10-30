package com.ims.nslmotors.repository.admin;

import com.ims.nslmotors.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminOrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {

    // Sipariş numaras?na g?re kontrol i?in
    Optional<Order> findByOrderNumber(String orderNumber);
}