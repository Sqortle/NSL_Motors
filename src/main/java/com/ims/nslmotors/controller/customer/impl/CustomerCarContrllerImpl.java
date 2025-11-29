// CustomerCarContrllerImpl.java
package com.ims.nslmotors.controller.customer.impl;

import com.ims.nslmotors.controller.customer.ICustomerCarController;
import com.ims.nslmotors.dto.customer.DtoCustomerCar;
import com.ims.nslmotors.services.customer.ICustomerCarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class CustomerCarContrllerImpl implements ICustomerCarController {

    private final ICustomerCarService customerCarService;

    // --- WEB PAGES ---
    
    // Ana Sayfa
    @GetMapping
    public String home(Model model) {
        Map<String, List<DtoCustomerCar>> catalog = customerCarService.getCarCatalogGroupedByMake();
        model.addAttribute("catalog", catalog);
        model.addAttribute("title", "Ana Sayfa");
        return "index";
    }

    // Katalog Sayfası
    @GetMapping("/catalog")
    public String catalog(Model model) {
        Map<String, List<DtoCustomerCar>> catalog = customerCarService.getCarCatalogGroupedByMake();
        model.addAttribute("catalog", catalog);
        model.addAttribute("title", "Katalog");
        return "catalog";
    }

    // Araba Detay Sayfası
    @GetMapping("/car/{id}")
    public String carDetail(@PathVariable Long id, Model model) {
        model.addAttribute("carId", id);
        model.addAttribute("title", "Araba Detayı");
        return "car-detail";
    }

    // --- REST API ---
    
    // REST API: Araba Kataloğu (Markaya Göre Gruplanmış)
    // URL: GET /api/customer/cars
    @Override
    @GetMapping("/api/customer/cars")
    @ResponseBody
    public ResponseEntity<Map<String, List<DtoCustomerCar>>> getCarCatalog() {
        Map<String, List<DtoCustomerCar>> catalog = customerCarService.getCarCatalogGroupedByMake();
        return ResponseEntity.ok(catalog);
    }
}