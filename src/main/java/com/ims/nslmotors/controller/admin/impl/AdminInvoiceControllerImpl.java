package com.ims.nslmotors.controller.admin.impl;

import com.ims.nslmotors.controller.admin.IAdminInvoiceController;
import com.ims.nslmotors.dto.admin.DtoAdminInvoice;
import com.ims.nslmotors.dto.admin.DtoAdminInvoiceIU;
import com.ims.nslmotors.services.admin.IAdminInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/invoices") // Ana yol: /api/admin/invoices
@RequiredArgsConstructor
public class AdminInvoiceControllerImpl implements IAdminInvoiceController {

    private final IAdminInvoiceService invoiceService;

    // --- CRUD: READ (Sayfalandırma ve Esnek Arama) ---
    @Override
    @GetMapping("/list")
    public ResponseEntity<Page<DtoAdminInvoice>> getInvoices(
            @ModelAttribute DtoAdminInvoice criteria,
            Pageable pageable) {

        Page<DtoAdminInvoice> invoicePage = invoiceService.getInvoices(criteria, pageable);

        return ResponseEntity.ok(invoicePage);
    }


}