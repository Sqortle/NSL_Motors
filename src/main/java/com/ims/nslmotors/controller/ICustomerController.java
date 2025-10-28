package com.ims.nslmotors.controller;

import com.ims.nslmotors.dto.DtoCustomer;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICustomerController {
    ResponseEntity<List<DtoCustomer>> getAllUsers();
}
