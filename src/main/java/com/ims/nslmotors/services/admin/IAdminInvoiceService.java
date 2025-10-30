package com.ims.nslmotors.services.admin;

import com.ims.nslmotors.dto.admin.DtoAdminInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAdminInvoiceService {

    // CRUD: READ - Sayfalandırma ve Arama
    Page<DtoAdminInvoice> getInvoices(DtoAdminInvoice criteria, Pageable pageable);
    //Invoice CRUD işlemlerinin hepsi Order tarafından yapılacaktır GET dışında
}