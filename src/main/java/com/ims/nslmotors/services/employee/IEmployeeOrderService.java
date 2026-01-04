package com.ims.nslmotors.services.employee;

import com.ims.nslmotors.dto.employee.DtoEmployeeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IEmployeeOrderService {

    /**
     * Teknisyene ait, bugün ve sonrası için atanmış siparişleri sayfalı olarak çeker.
     * @param technicianId Mevcut teknisyenin ID'si.
     * @param pageable Sayfalama bilgisi (sayfa no, boyut, sıralama).
     * @return DtoEmployeeOrder listesinin sayfalı görünümü.
     */
    Page<DtoEmployeeOrder> getPagedFutureOrdersForTechnician(
            Long technicianId,
            Pageable pageable
    );

    /**
     * Sipariş durumunu günceller (sadece kendisine atanan siparişler için).
     * @param orderId Sipariş ID'si.
     * @param technicianId Teknisyen ID'si (yetki kontrolü için).
     * @param newStatus Yeni durum.
     */
    void updateOrderStatus(Long orderId, Long technicianId, String newStatus);
}