// ICustomerOrderService.java
package com.ims.nslmotors.services.customer;

import com.ims.nslmotors.dto.customer.DtoCustomerOrder;
import java.util.List;

public interface ICustomerOrderService {

    // Müşteri ID'sine ait tüm siparişleri getirir
    List<DtoCustomerOrder> getMyOrders(Long customerId);

    // YENİ: Müşteri sipariş verir (Müşteri kendi ID'sini gönderemeyeceği için, sadece sipariş detayları gelir)
    // DtoCustomerOrder createNewOrder(DtoCustomerOrderCreation dto, Long customerId);
}