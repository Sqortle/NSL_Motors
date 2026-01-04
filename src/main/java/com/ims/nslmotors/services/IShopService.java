package com.ims.nslmotors.services;

import com.ims.nslmotors.dto.DtoShop;
import java.util.List;

public interface IShopService {
    
    // Tüm aktif dükkanları getir
    List<DtoShop> getAllActiveShops();
    
    // ID'ye göre dükkan getir
    DtoShop getShopById(Long id);
    
    // Şehre göre aktif dükkanları getir
    List<DtoShop> getShopsByCity(String city);
}
