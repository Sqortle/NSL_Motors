package com.ims.nslmotors.controller.web;

import com.ims.nslmotors.dto.customer.DtoCustomerCar;
import com.ims.nslmotors.services.customer.ICustomerCarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class WebController {

    private final ICustomerCarService customerCarService;

    @GetMapping
    public String home(Model model) {
        Map<String, List<DtoCustomerCar>> catalog = customerCarService.getCarCatalogGroupedByMake();
        model.addAttribute("catalog", catalog);
        model.addAttribute("title", "Ana Sayfa");
        return "index";
    }

    @GetMapping("/catalog")
    public String catalog(Model model) {
        Map<String, List<DtoCustomerCar>> catalog = customerCarService.getCarCatalogGroupedByMake();
        model.addAttribute("catalog", catalog);
        model.addAttribute("title", "Katalog");
        return "catalog";
    }

    @GetMapping("/car/{id}")
    public String carDetail(@PathVariable Long id, Model model) {
        // Car detail için service method'u eklenebilir
        model.addAttribute("carId", id);
        model.addAttribute("title", "Araba Detayı");
        return "car-detail";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Hakkımızda");
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("title", "İletişim");
        return "contact";
    }
}

