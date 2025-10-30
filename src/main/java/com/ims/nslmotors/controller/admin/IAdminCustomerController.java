package com.ims.nslmotors.controller.admin;

import com.ims.nslmotors.dto.admin.DtoAdminCustomer;
import com.ims.nslmotors.dto.admin.DtoAdminCustomerIU; // Yeni import
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid; // Validation i?in

import java.util.List;

public interface IAdminCustomerController {

    ResponseEntity<Page<DtoAdminCustomer>> getCustomers(DtoAdminCustomer dtoAdminCustomer, Pageable pageable);

    ResponseEntity<DtoAdminCustomer> createCustomer(@Valid DtoAdminCustomerIU customerCreationDto);

    ResponseEntity<List<DtoAdminCustomer>> createCustomersBulk(@Valid List<DtoAdminCustomerIU> customerList);

    ResponseEntity<DtoAdminCustomer> updateCustomer(Long id, @Valid DtoAdminCustomerIU updateDto);

    ResponseEntity<Void> deleteCustomer(Long id);
}
