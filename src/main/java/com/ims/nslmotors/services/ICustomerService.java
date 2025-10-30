package com.ims.nslmotors.services;

import com.ims.nslmotors.dto.DtoCustomer;
import com.ims.nslmotors.dto.DtoCustomerIU; // Yeni import
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ICustomerService {
    Page<DtoCustomer> getCustomersWithPaginationAndSearch(DtoCustomer dtoCustomer, Pageable pageable);

    DtoCustomer createCustomer(DtoCustomerIU customerCreationDto);

    List<DtoCustomer> createCustomersBulk(List<DtoCustomerIU> customerCreationDtos);

    DtoCustomer updateCustomer(Long id, DtoCustomerIU updateDto);

    void deleteCustomer(Long id);
}