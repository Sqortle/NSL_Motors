// ICustomerCarController.java
package com.ims.nslmotors.controller.customer;

import com.ims.nslmotors.dto.customer.DtoCustomerCar;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;

public interface ICustomerCarController {

    // Markaya göre gruplanmış harita döndürür
    ResponseEntity<Map<String, List<DtoCustomerCar>>> getCarCatalog();
}