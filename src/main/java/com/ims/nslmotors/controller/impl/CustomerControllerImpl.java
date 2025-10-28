package com.ims.nslmotors.controller.impl;

import com.ims.nslmotors.controller.ICustomerController;
import com.ims.nslmotors.dto.DtoCustomer;
import com.ims.nslmotors.dto.DtoCustomerIU; // Yeni import
import com.ims.nslmotors.services.ICustomerService;
import jakarta.validation.Valid; // Validation'lar? ?al??t?rmak i?in
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // HTTP 201 Created d?nd?rmek i?in
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // PostMapping ve RequestBody i?in

import java.util.List;

@RestController
@RequestMapping("/api/admin/customers") // Yolu /api/admin/customers olarak d?zenledik
@RequiredArgsConstructor
public class CustomerControllerImpl implements ICustomerController {

    private final ICustomerService customerService;

    // CRUD: READ - Tüm kullanıcıları getir
    @Override
    @GetMapping("/list")
    public ResponseEntity<List<DtoCustomer>> getAllUsers() {
        List<DtoCustomer> users = customerService.getAllCustomers();
        return ResponseEntity.ok(users); // HTTP 200 OK
    }

    // YENİ CRUD: CREATE - Yeni müşteri oluştur
    @Override
    @PostMapping("/add")
    // @Valid: DTO'daki validation kurallarını (NotBlank, Email, Size) aktif eder.
    public ResponseEntity<DtoCustomer> createCustomer(@Valid @RequestBody DtoCustomerIU customerCreationDto) {
        DtoCustomer createdCustomer = customerService.createCustomer(customerCreationDto);
        // Yeni bir kaynak oluşturulduğunda HTTP 201 Created döndürülür.
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
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