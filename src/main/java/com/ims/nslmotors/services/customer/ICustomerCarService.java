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
}