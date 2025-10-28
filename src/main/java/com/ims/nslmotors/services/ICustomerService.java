package com.ims.nslmotors.services;

import com.ims.nslmotors.dto.DtoCustomer;

import java.util.List;

public interface ICustomerService {
    List<DtoCustomer> getAllCustomers();
}
