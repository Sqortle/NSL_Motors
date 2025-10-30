package com.ims.nslmotors.controller;

import com.ims.nslmotors.dto.DtoCustomer;
import com.ims.nslmotors.dto.DtoCustomerIU; // Yeni import
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid; // Validation i?in
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ICustomerController {

    ResponseEntity<Page<DtoCustomer>> getCustomers(DtoCustomer dtoCustomer, Pageable pageable);

    ResponseEntity<DtoCustomer> createCustomer(@Valid DtoCustomerIU customerCreationDto);

    ResponseEntity<List<DtoCustomer>> createCustomersBulk(@Valid List<DtoCustomerIU> customerList);

    ResponseEntity<DtoCustomer> updateCustomer(Long id, @Valid DtoCustomerIU updateDto);

    ResponseEntity<Void> deleteCustomer(Long id);
}
