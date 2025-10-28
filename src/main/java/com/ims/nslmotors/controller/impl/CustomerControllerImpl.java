package com.ims.nslmotors.controller.impl;

import com.ims.nslmotors.controller.ICustomerController;
import com.ims.nslmotors.dto.DtoCustomer;
import com.ims.nslmotors.repository.CustomerRepository;
import com.ims.nslmotors.services.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class CustomerControllerImpl implements ICustomerController {

    private final ICustomerService customerService;

    @Override
    @GetMapping("/customers")
    public ResponseEntity<List<DtoCustomer>> getAllUsers() {
        List<DtoCustomer> users = customerService.getAllCustomers();

        return ResponseEntity.ok(users); // HTTP 200 OK
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