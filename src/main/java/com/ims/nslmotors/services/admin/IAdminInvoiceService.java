package com.ims.nslmotors.services.admin;

import com.ims.nslmotors.dto.admin.DtoAdminInvoice;
import com.ims.nslmotors.dto.admin.DtoAdminInvoiceIU;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IAdminInvoiceService {

    // CRUD: READ - Sayfalandırma ve Arama
    Page<DtoAdminInvoice> getInvoices(DtoAdminInvoice criteria, Pageable pageable);
    //Invoice CRUD işlemlerinin hepsi Order tarafından yapılacaktır GET dışında
}