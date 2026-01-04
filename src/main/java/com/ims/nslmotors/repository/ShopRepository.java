package com.ims.nslmotors.repository;

import com.ims.nslmotors.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    
    // Aktif dükkanları getir
    List<Shop> findByIsActiveTrue();
    
    // Şehre göre aktif dükkanları getir
    List<Shop> findByCityAndIsActiveTrue(String city);
}
