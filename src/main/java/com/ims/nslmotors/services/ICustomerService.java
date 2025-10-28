package com.ims.nslmotors.services;

import com.ims.nslmotors.dto.DtoCustomer;
import com.ims.nslmotors.dto.DtoCustomerIU; // Yeni import

import java.util.List;

public interface ICustomerService {
    List<DtoCustomer> getAllCustomers();

    // YENİ: Müşteri oluşturma metodu (CREATE)
    DtoCustomer createCustomer(DtoCustomerIU customerCreationDto);
}