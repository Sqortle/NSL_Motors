package com.ims.nslmotors.controller.admin;

import com.ims.nslmotors.dto.admin.DtoAdminInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface IAdminInvoiceController {

    // CRUD: READ
    ResponseEntity<Page<DtoAdminInvoice>> getInvoices(DtoAdminInvoice criteria, Pageable pageable);

}