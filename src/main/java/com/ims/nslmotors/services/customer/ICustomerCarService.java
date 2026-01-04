// ICustomerCarService.java
package com.ims.nslmotors.services.customer;

import com.ims.nslmotors.dto.customer.DtoCustomerCar;
import java.util.List;
import java.util.Map;

public interface ICustomerCarService {

    // Arabaları markaya göre gruplayarak döndüren metot (Map<Marka, List<Araba>>)
    Map<String, List<DtoCustomerCar>> getCarCatalogGroupedByMake();

    // Tüm arabaları düz bir liste halinde döndürür (Gerekirse)
    List<DtoCustomerCar> getAllCarsForCustomer();

    // Markaları ve makeImageUrl'lerini döndüren metot (Map<Marka, makeImageUrl>)
    Map<String, String> getMakesWithImages();

    // Belirli bir markaya ait arabaları döndürür
    List<DtoCustomerCar> getCarsByMake(String make);

    // ID'ye göre tek bir araba döndürür
    DtoCustomerCar getCarById(Long id);
}