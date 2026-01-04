package com.ims.nslmotors.services.employee.impl;

import com.ims.nslmotors.model.Order;
import com.ims.nslmotors.repository.admin.AdminOrderRepository;
import com.ims.nslmotors.services.employee.IEmployeeOrderService;
import com.ims.nslmotors.dto.employee.DtoEmployeeOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeOrderServiceImpl implements IEmployeeOrderService {

    private final AdminOrderRepository orderRepository;

    @Override
    public Page<DtoEmployeeOrder> getPagedFutureOrdersForTechnician(Long technicianId, Pageable pageable) {
        log.debug("getPagedFutureOrdersForTechnician çağrıldı - TechnicianId: {}", technicianId);
        // Sadece bugün ve sonrası için siparişleri görmek
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);

        Page<Order> orderEntities = orderRepository.findByTechnicianIdAndOrderDateGreaterThanEqual(
                technicianId,
                startOfToday,
                pageable
        );

        log.debug("Bulunan sipariş sayısı: {}", orderEntities.getTotalElements());
        return orderEntities.map(this::convertToDto);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, Long technicianId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Sipariş bulunamadı."));

        // Yetki kontrolü - sadece kendisine atanan siparişleri güncelleyebilir
        if (order.getTechnician() == null || !order.getTechnician().getId().equals(technicianId)) {
            throw new RuntimeException("Bu sipariş size atanmamış. Durum güncelleyemezsiniz.");
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    private DtoEmployeeOrder convertToDto(Order order) {
        DtoEmployeeOrder dto = new DtoEmployeeOrder();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        
        if (order.getCustomer() != null) {
            dto.setCustomerFullName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());
        }
        
        if (order.getCarModel() != null) {
            dto.setCarMake(order.getCarModel().getMake());
            dto.setCarModel(order.getCarModel().getModel());
        }
        
        dto.setStageSelected(order.getStageSelected());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        
        if (order.getInvoice() != null) {
            dto.setInvoiceStatus(order.getInvoice().getStatus() != null ? order.getInvoice().getStatus() : "Yok");
        } else {
            dto.setInvoiceStatus("Yok");
        }
        
        return dto;
    }
}