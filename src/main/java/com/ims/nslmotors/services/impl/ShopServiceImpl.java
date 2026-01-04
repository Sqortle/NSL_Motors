package com.ims.nslmotors.services.impl;

import com.ims.nslmotors.dto.DtoShop;
import com.ims.nslmotors.model.Shop;
import com.ims.nslmotors.repository.ShopRepository;
import com.ims.nslmotors.services.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements IShopService {
    
    private final ShopRepository shopRepository;
    
    // Helper: Entity'den DTO'ya dönüşüm
    private DtoShop convertToDto(Shop shop) {
        DtoShop dto = new DtoShop();
        BeanUtils.copyProperties(shop, dto);
        return dto;
    }
    
    @Override
    public List<DtoShop> getAllActiveShops() {
        return shopRepository.findByIsActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public DtoShop getShopById(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dükkan bulunamadı: ID = " + id));
        return convertToDto(shop);
    }
    
    @Override
    public List<DtoShop> getShopsByCity(String city) {
        return shopRepository.findByCityAndIsActiveTrue(city).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
