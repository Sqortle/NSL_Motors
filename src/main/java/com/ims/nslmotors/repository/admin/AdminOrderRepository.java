package com.ims.nslmotors.repository.admin;

import com.ims.nslmotors.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminOrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {

    // Sipariş numarasına göre kontrol için
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCustomerId(Long customerId);

    // YENİ GEREKSİNİM: Çalışanın kendi ID'sine bağlı, bugünden itibaren olan siparişleri çeker.
    Page<Order> findByTechnicianIdAndOrderDateGreaterThanEqual(
            Long technicianId,
            LocalDateTime localDateTime,
            Pageable pageable
    );
}