// CustomerCarServiceImpl.java
package com.ims.nslmotors.services.customer.impl;

import com.ims.nslmotors.dto.customer.DtoCustomerCar;
import com.ims.nslmotors.model.Car;
import com.ims.nslmotors.repository.admin.AdminCarRepository;
import com.ims.nslmotors.services.customer.ICustomerCarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerCarServiceImpl implements ICustomerCarService {

    // Admin'in kullandığı Repository'yi paylaşıyoruz
    private final AdminCarRepository carRepository;

    // --- Helper: Entity'den Customer DTO'ya Dönüşüm ---
    private DtoCustomerCar convertToDto(Car car) {
        DtoCustomerCar dto = new DtoCustomerCar();
        BeanUtils.copyProperties(car, dto);

        // Fiyatları manuel set et
        dto.setStage1Price(car.getStage1Price());
        dto.setStage2Price(car.getStage2Price());

        return dto;
    }

    // --- READ: Tüm arabaları düz bir liste olarak döndürür ---
    @Override
    public List<DtoCustomerCar> getAllCarsForCustomer() {
        return carRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // --- READ: Markaya göre gruplanmış Map döndürür (İstenen Ana Metot) ---
    @Override
    public Map<String, List<DtoCustomerCar>> getCarCatalogGroupedByMake() {
        // Tüm arabaları çek, DTO'ya dönüştür ve make (marka) alanına göre grupla
        return carRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.groupingBy(DtoCustomerCar::getMake));
    }
}