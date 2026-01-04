package com.ims.nslmotors.repository.admin;

import com.ims.nslmotors.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminInvoiceRepository extends JpaRepository<Invoice, Long>,
        JpaSpecificationExecutor<Invoice> {
    // Fatura ID'si, Sipariş ID'si ile aynıdır.
    
    // Order ile birlikte yükle (lazy loading sorununu önlemek için)
    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.order WHERE i.id = :id")
    Invoice findByIdWithOrder(@Param("id") Long id);
    
    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.order")
    List<Invoice> findAllWithOrder();
}