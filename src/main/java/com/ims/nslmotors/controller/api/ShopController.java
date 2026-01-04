package com.ims.nslmotors.controller.api;

import com.ims.nslmotors.dto.DtoShop;
import com.ims.nslmotors.services.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ShopController {
    
    private final IShopService shopService;
    
    // Tüm aktif dükkanları getir
    @GetMapping
    public ResponseEntity<List<DtoShop>> getAllShops() {
        List<DtoShop> shops = shopService.getAllActiveShops();
        return ResponseEntity.ok(shops);
    }
    
    // ID'ye göre dükkan getir
    @GetMapping("/{id}")
    public ResponseEntity<DtoShop> getShopById(@PathVariable Long id) {
        DtoShop shop = shopService.getShopById(id);
        return ResponseEntity.ok(shop);
    }
    
    // Şehre göre dükkanları getir
    @GetMapping("/city/{city}")
    public ResponseEntity<List<DtoShop>> getShopsByCity(@PathVariable String city) {
        List<DtoShop> shops = shopService.getShopsByCity(city);
        return ResponseEntity.ok(shops);
    }
}
