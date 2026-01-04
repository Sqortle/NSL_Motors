package com.ims.nslmotors.repository.admin;

import com.ims.nslmotors.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Query("SELECT o FROM Order o WHERE o.technician IS NOT NULL AND o.technician.id = :technicianId AND o.orderDate >= :startDate")
    Page<Order> findByTechnicianIdAndOrderDateGreaterThanEqual(
            @Param("technicianId") Long technicianId,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable
    );

    // Teknisyene ait tüm siparişleri çeker
    @Query("SELECT o FROM Order o WHERE o.technician IS NOT NULL AND o.technician.id = :technicianId")
    Page<Order> findByTechnicianId(@Param("technicianId") Long technicianId, Pageable pageable);
}