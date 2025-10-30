package com.ims.nslmotors.controller.admin.impl;

import com.ims.nslmotors.controller.admin.IAdminCustomerController;
import com.ims.nslmotors.dto.admin.DtoAdminCustomer;
import com.ims.nslmotors.dto.admin.DtoAdminCustomerIU; // Yeni import
import com.ims.nslmotors.services.admin.IAdminCustomerService;
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
public class AdminCustomerControllerImpl implements IAdminCustomerController {

    @Autowired
    private IAdminCustomerService customerService;

    @Override
    @GetMapping("/list")
    public ResponseEntity<Page<DtoAdminCustomer>> getCustomers(
            // Tüm arama filtrelerini alır (firstName, email, vb.)
            @ModelAttribute DtoAdminCustomer dtoAdminCustomer,
            // page, size, sort parametrelerini otomatik doldurur
            Pageable pageable) {

        Page<DtoAdminCustomer> customerPage = customerService.getCustomersWithPaginationAndSearch(dtoAdminCustomer, pageable);

        return ResponseEntity.ok(customerPage); // HTTP 200 OK
    }

    @Override
    @PostMapping("/add")
    // @Valid: DTO'daki validation kurallarını (NotBlank, Email, Size) aktif eder.
    public ResponseEntity<DtoAdminCustomer> createCustomer(@Valid @RequestBody DtoAdminCustomerIU customerCreationDto) {
        DtoAdminCustomer createdCustomer = customerService.createCustomer(customerCreationDto);
        // Yeni bir kaynak oluşturulduğunda HTTP 201 Created döndürülür.
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @Override
    @PostMapping("/bulk") // Yolu: /api/admin/customers/bulk
    public ResponseEntity<List<DtoAdminCustomer>> createCustomersBulk(
            // @RequestBody: JSON dizisini otomatik List<DtoCustomerIU>'ya dönüştürür
            @Valid @RequestBody List<DtoAdminCustomerIU> customerList) {

        List<DtoAdminCustomer> createdList = customerService.createCustomersBulk(customerList);

        // Toplu işlemde de HTTP 201 Created döndürülür.
        return new ResponseEntity<>(createdList, HttpStatus.CREATED);
    }

    @Override
    @PutMapping("update/{id}")
    public ResponseEntity<DtoAdminCustomer> updateCustomer(@PathVariable Long id, @Valid @RequestBody DtoAdminCustomerIU updateDto){

        DtoAdminCustomer updatedCustomer = customerService.updateCustomer(id, updateDto);
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