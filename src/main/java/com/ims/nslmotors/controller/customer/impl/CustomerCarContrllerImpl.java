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

    // Katalog Sayfası - Markalar Grid
    @GetMapping("/catalog")
    public String catalog(Model model) {
        Map<String, String> makesWithImages = customerCarService.getMakesWithImages();
        model.addAttribute("makesWithImages", makesWithImages);
        model.addAttribute("title", "Katalog");
        return "catalog";
    }

    // Marka Modelleri Sayfası
    @GetMapping("/catalog/{make}")
    public String catalogByMake(@PathVariable String make, Model model) {
        List<DtoCustomerCar> cars = customerCarService.getCarsByMake(make);
        model.addAttribute("make", make);
        model.addAttribute("cars", cars);
        model.addAttribute("title", make + " Modelleri");
        return "catalog-models";
    }

    // Araba Detay Sayfası
    @GetMapping("/car/{id}")
    public String carDetail(@PathVariable Long id, Model model) {
        DtoCustomerCar car = customerCarService.getCarById(id);
        model.addAttribute("car", car);
        model.addAttribute("title", car.getMake() + " " + car.getModel() + " - Detaylar");
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