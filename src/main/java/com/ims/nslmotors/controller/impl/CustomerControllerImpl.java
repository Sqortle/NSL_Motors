package com.ims.nslmotors.controller.impl;

import com.ims.nslmotors.controller.ICustomerController;
import com.ims.nslmotors.dto.DtoCustomer;
import com.ims.nslmotors.dto.DtoCustomerIU; // Yeni import
import com.ims.nslmotors.services.ICustomerService;
import jakarta.validation.Valid; // Validation'lar? ?al??t?rmak i?in
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus; // HTTP 201 Created d?nd?rmek i?in
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // PostMapping ve RequestBody i?in

import java.util.List;

@RestController
@RequestMapping("/api/admin/customers") // Yolu /api/admin/customers olarak d?zenledik
@RequiredArgsConstructor
public class CustomerControllerImpl implements ICustomerController {

    @Autowired
    private ICustomerService customerService;

    @Override
    @GetMapping("/list")
    public ResponseEntity<Page<DtoCustomer>> getCustomers(
            // Tüm arama filtrelerini alır (firstName, email, vb.)
            @ModelAttribute DtoCustomer dtoCustomer,
            // page, size, sort parametrelerini otomatik doldurur
            Pageable pageable) {

        Page<DtoCustomer> customerPage = customerService.getCustomersWithPaginationAndSearch(dtoCustomer, pageable);

        return ResponseEntity.ok(customerPage); // HTTP 200 OK
    }

    @Override
    @PostMapping("/add")
    // @Valid: DTO'daki validation kurallarını (NotBlank, Email, Size) aktif eder.
    public ResponseEntity<DtoCustomer> createCustomer(@Valid @RequestBody DtoCustomerIU customerCreationDto) {
        DtoCustomer createdCustomer = customerService.createCustomer(customerCreationDto);
        // Yeni bir kaynak oluşturulduğunda HTTP 201 Created döndürülür.
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @PostMapping("/bulk") // Yolu: /api/admin/customers/bulk
    public ResponseEntity<List<DtoCustomer>> createCustomersBulk(
            // @RequestBody: JSON dizisini otomatik List<DtoCustomerIU>'ya dönüştürür
            @Valid @RequestBody List<DtoCustomerIU> customerList) {

        List<DtoCustomer> createdList = customerService.createCustomersBulk(customerList);

        // Toplu işlemde de HTTP 201 Created döndürülür.
        return new ResponseEntity<>(createdList, HttpStatus.CREATED);
    }

    @Override
    @PutMapping("update/{id}")
    public ResponseEntity<DtoCustomer> updateCustomer(@PathVariable Long id, @Valid @RequestBody DtoCustomerIU updateDto){

        DtoCustomer updatedCustomer = customerService.updateCustomer(id, updateDto);
        return ResponseEntity.ok(updatedCustomer);
    }

    @Override
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id){
        customerService.deleteCustomer(id);

        return ResponseEntity.noContent().build();
    }

}
/*
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
                <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

 */