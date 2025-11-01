// CustomerOrderServiceImpl.java
package com.ims.nslmotors.services.customer.impl;

import com.ims.nslmotors.dto.customer.DtoCustomerOrder;
import com.ims.nslmotors.model.Order;
import com.ims.nslmotors.repository.admin.AdminOrderRepository;
import com.ims.nslmotors.services.customer.ICustomerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerOrderServiceImpl implements ICustomerOrderService {

    private final AdminOrderRepository orderRepository;
    // Not: Customer ve Car Repository'ler de laz?m olabilir (E?er burada CREATE operasyonu yap?lacaksa)

    // --- Helper: Entity'den DTO'ya Dönüşüm ---
    private DtoCustomerOrder convertToDto(Order order) {
        DtoCustomerOrder dto = new DtoCustomerOrder();
        BeanUtils.copyProperties(order, dto);

        // İlişkisel alanları doldur
        dto.setCarMakeAndModel(order.getCarModel().getMake() + " " + order.getCarModel().getModel());
        if (order.getTechnician() != null) {
            // Technician'ın shopName'ini ekliyoruz (Vendor senaryosundan gelen detay)
            dto.setTechnicianShopName(order.getTechnician().getShopName());
        }

        return dto;
    }

    // --- READ: Müşterinin Kendi Siparişlerini Çekme ---
    @Override
    public List<DtoCustomerOrder> getMyOrders(Long customerId) {

        // Repository'nin özel metodu ile sadece ilgili müşterinin siparişleri çekilir.
        List<Order> myOrders = orderRepository.findByCustomerId(customerId);

        return myOrders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}