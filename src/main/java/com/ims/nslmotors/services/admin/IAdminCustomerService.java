package com.ims.nslmotors.services.admin;

import com.ims.nslmotors.dto.admin.DtoAdminCustomer;
import com.ims.nslmotors.dto.admin.DtoAdminCustomerIU; // Yeni import
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdminCustomerService {
    Page<DtoAdminCustomer> getCustomersWithPaginationAndSearch(DtoAdminCustomer dtoAdminCustomer, Pageable pageable);

    DtoAdminCustomer createCustomer(DtoAdminCustomerIU customerCreationDto);

    List<DtoAdminCustomer> createCustomersBulk(List<DtoAdminCustomerIU> customerCreationDtos);

    DtoAdminCustomer updateCustomer(Long id, DtoAdminCustomerIU updateDto);

    void deleteCustomer(Long id);
}