package com.ims.nslmotors.services.employee;

import com.ims.nslmotors.dto.employee.DtoEmployeeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IEmployeeOrderService {

    /**
     * Teknisyene ait, bug?n ve sonras? i?in atanm?? sipari?leri sayfal? olarak ?eker.
     * @param technicianId Mevcut teknisyenin ID'si.
     * @param pageable Sayfalama bilgisi (sayfa no, boyut, s?ralama).
     * @return DtoEmployeeOrder listesinin sayfal? g?r?n?m?.
     */
    Page<DtoEmployeeOrder> getPagedFutureOrdersForTechnician(
            Long technicianId,
            Pageable pageable
    );
}