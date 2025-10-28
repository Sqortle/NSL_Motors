package com.ims.nslmotors.controller;

import com.ims.nslmotors.dto.DtoCustomer;
import com.ims.nslmotors.dto.DtoCustomerIU; // Yeni import
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid; // Validation i?in

import java.util.List;

public interface ICustomerController {
    ResponseEntity<List<DtoCustomer>> getAllUsers();

    // YENİ: Müşteri oluşturma (CREATE)
    ResponseEntity<DtoCustomer> createCustomer(DtoCustomerIU customerCreationDto);
}