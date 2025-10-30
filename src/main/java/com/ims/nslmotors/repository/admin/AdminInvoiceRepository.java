package com.ims.nslmotors.repository.admin;

import com.ims.nslmotors.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminInvoiceRepository extends JpaRepository<Invoice, Long>,
        JpaSpecificationExecutor<Invoice> {
    // Fatura ID'si, Sipariş ID'si ile aynıdır.
}