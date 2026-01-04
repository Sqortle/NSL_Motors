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
        dto.setStage3Price(car.getStage3Price());
        dto.setMakeImageUrl(car.getMakeImageUrl());

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

    // --- READ: Markaları ve makeImageUrl'lerini döndürür ---
    @Override
    public Map<String, String> getMakesWithImages() {
        // Tüm arabaları çek, markaya göre grupla ve her marka için makeImageUrl'i al
        // Aynı markaya ait arabalardan birinin makeImageUrl'ini al (genelde hepsi aynı olacak)
        return carRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    Car::getMake,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        cars -> cars.stream()
                                .filter(car -> car.getMakeImageUrl() != null && !car.getMakeImageUrl().isEmpty())
                                .findFirst()
                                .map(Car::getMakeImageUrl)
                                .orElse(null)
                    )
                ));
    }

    // --- READ: Belirli bir markaya ait arabaları döndürür ---
    @Override
    public List<DtoCustomerCar> getCarsByMake(String make) {
        return carRepository.findByMake(make).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // --- READ: ID'ye göre tek bir araba döndürür ---
    @Override
    public DtoCustomerCar getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Araba bulunamadı: ID = " + id));
        return convertToDto(car);
    }
}