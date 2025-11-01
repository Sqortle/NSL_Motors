// CustomerCarContrllerImpl.java
package com.ims.nslmotors.controller.customer.impl;

import com.ims.nslmotors.controller.customer.ICustomerCarController;
import com.ims.nslmotors.dto.customer.DtoCustomerCar;
import com.ims.nslmotors.services.customer.ICustomerCarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars") // MÜŞTERİ YOLU
@RequiredArgsConstructor
public class CustomerCarContrllerImpl implements ICustomerCarController {

    private final ICustomerCarService customerCarService;

    // --- READ: Araba Kataloğu (Markaya Göre Gruplanmış) ---
    // URL: GET /api/cars
    @Override
    @GetMapping
    public ResponseEntity<Map<String, List<DtoCustomerCar>>> getCarCatalog() {

        Map<String, List<DtoCustomerCar>> catalog = customerCarService.getCarCatalogGroupedByMake();

        return ResponseEntity.ok(catalog);
    }
}