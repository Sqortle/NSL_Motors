package com.ims.nslmotors.services.employee.impl;

import com.ims.nslmotors.model.Order;
import com.ims.nslmotors.repository.admin.AdminOrderRepository;
import com.ims.nslmotors.services.employee.IEmployeeOrderService;
import com.ims.nslmotors.dto.employee.DtoEmployeeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class EmployeeOrderServiceImpl implements IEmployeeOrderService {

    private final AdminOrderRepository orderRepository;
    // Di?er Repository ve Mapper enjeksiyonlar? burada olmal?

    public EmployeeOrderServiceImpl(AdminOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Page<DtoEmployeeOrder> getPagedFutureOrdersForTechnician(Long technicianId, Pageable pageable) {

        // Gereksinim: Sadece bug?n ve sonras? i?in sipari?leri g?rmek.
        // LocalDateTime.of(LocalDate.now(), LocalTime.MIN) ile bug?n?n ilk an?n? (00:00:00) al?r?z.
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);

        // Repository'den veriyi ?eker
        Page<Order> orderEntities = orderRepository.findByTechnicianIdAndOrderDateGreaterThanEqual(
                technicianId,
                startOfToday,
                pageable
        );

        // Entity listesini DTO listesine d?n??t?r?r (Mapper kullan?m? burada olur)
        // D?n??t?rme (Mapping) mant??? ?u an atlanm??t?r.
        return orderEntities.map(this::convertToDto);
    }

    // Ge?ici DTO d?n??t?rme metodu
    private DtoEmployeeOrder convertToDto(Order order) {
        // ... Ger?ek DTO d?n??t?rme mant??? buraya gelir ...
        return new DtoEmployeeOrder();
    }
}