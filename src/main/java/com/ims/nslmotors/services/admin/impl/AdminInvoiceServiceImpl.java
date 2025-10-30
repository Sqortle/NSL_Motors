package com.ims.nslmotors.services.admin.impl;

import com.ims.nslmotors.dto.admin.DtoAdminInvoice;
import com.ims.nslmotors.model.Invoice;
import com.ims.nslmotors.repository.admin.AdminInvoiceRepository;
import com.ims.nslmotors.repository.admin.AdminOrderRepository; // Order'a erişim için
import com.ims.nslmotors.services.admin.IAdminInvoiceService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminInvoiceServiceImpl implements IAdminInvoiceService {

    private final AdminInvoiceRepository invoiceRepository;
    private final AdminOrderRepository orderRepository;

    // --- Helper: Entity'den DTO'ya Dönüşüm ---
    private DtoAdminInvoice convertToDto(Invoice invoice) {
        DtoAdminInvoice dto = new DtoAdminInvoice();
        BeanUtils.copyProperties(invoice, dto);

        // İlişkisel OrderNumber bilgisini ekle
        dto.setOrderId(invoice.getOrder().getId());
        dto.setOrderNumber(invoice.getOrder().getOrderNumber());

        return dto;
    }

    // --- CRUD: READ (Sayfalandırma ve Esnek Arama) ---
    @Override
    public Page<DtoAdminInvoice> getInvoices(DtoAdminInvoice criteria, Pageable pageable) {
        Specification<Invoice> specification = buildSpecification(criteria);
        Page<Invoice> invoicePage = invoiceRepository.findAll(specification, pageable);
        return invoicePage.map(this::convertToDto);
    }

    // Dinamik Filtreleme Mantığı (Specification)
    private Specification<Invoice> buildSpecification(DtoAdminInvoice criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Order Number ARAMA (Order Entity'sinden çekilir)
            if (criteria.getOrderNumber() != null && !criteria.getOrderNumber().isEmpty()) {
                // root.get("order") ile Order Entity'sine ula??l?r
                predicates.add(criteriaBuilder.like(root.get("order").get("orderNumber"),
                        "%" + criteria.getOrderNumber() + "%"));
            }

            // 2. Order ID ARAMA
            if (criteria.getOrderId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("order").get("id"), criteria.getOrderId()));
            }

            // 3. Durum (Status) filtresi
            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


}