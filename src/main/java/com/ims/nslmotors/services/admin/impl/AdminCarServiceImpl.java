package com.ims.nslmotors.services.admin.impl;

import com.ims.nslmotors.dto.admin.DtoAdminCar;
import com.ims.nslmotors.dto.admin.DtoAdminCarIU;
import com.ims.nslmotors.model.Car;
import com.ims.nslmotors.repository.admin.AdminCarRepository;
import com.ims.nslmotors.services.admin.IAdminCarService;
import jakarta.persistence.criteria.Predicate; // Eklendi
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification; // Eklendi
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCarServiceImpl implements IAdminCarService {

    private final AdminCarRepository adminCarRepository;

    // --- Helper Metot ---
    private DtoAdminCar convertToDto(Car carModel) {
        DtoAdminCar dto = new DtoAdminCar();
        BeanUtils.copyProperties(carModel, dto);
        return dto;
    }

    // --- CRUD: READ (Sayfaland?rma ve Esnek Arama) ---
    @Override
    public Page<DtoAdminCar> getCars(DtoAdminCar criteria, Pageable pageable) {

        Specification<Car> specification = buildSpecification(criteria);

        Page<Car> carModelPage = adminCarRepository.findAll(specification, pageable);

        return carModelPage.map(this::convertToDto);
    }

    // Dinamik Filtreleme Mantığı (Specification)
    private Specification<Car> buildSpecification(DtoAdminCar criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ID ARAMA
            if (criteria.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), criteria.getId()));
            }

            // TEXT ALANLARI (make, model)
            if (criteria.getMake() != null && !criteria.getMake().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("make")),
                        "%" + criteria.getMake().toLowerCase() + "%"));
            }
            if (criteria.getModel() != null && !criteria.getModel().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("model")),
                        "%" + criteria.getModel().toLowerCase() + "%"));
            }

            // SAYISAL ALANLAR (year, hp alanlar?)
            if (criteria.getYear() != null) {
                predicates.add(criteriaBuilder.equal(root.get("year"), criteria.getYear()));
            }
            if (criteria.getStockHp() != null) {
                predicates.add(criteriaBuilder.equal(root.get("stockHp"), criteria.getStockHp()));
            }
            // Di?er HP alanlar? (stage1Hp, stage2Hp, stage3Hp) da benzer ?ekilde eklenebilir.
            if (criteria.getStage1Hp() != null) {
                predicates.add(criteriaBuilder.equal(root.get("stage1Hp"), criteria.getStage1Hp()));
            }
            // ...

            // T?m ko?ullar? AND ile birle?tir
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // --- CRUD: CREATE (Tekil) ---
    @Override
    public DtoAdminCar createCar(DtoAdminCarIU carCreationDto) {
        Car car = new Car();
        BeanUtils.copyProperties(carCreationDto, car);

        Car savedCar = adminCarRepository.save(car);
        return convertToDto(savedCar);
    }

    // --- CRUD: CREATE (Toplu) ---
    @Override
    public List<DtoAdminCar> createCarsBulk(List<DtoAdminCarIU> carCreationDtos) {
        List<Car> carsToSave = carCreationDtos.stream()
                .map(dto -> {
                    Car carModel = new Car();
                    BeanUtils.copyProperties(dto, carModel);
                    return carModel;
                })
                .collect(Collectors.toList());

        List<Car> savedCars = adminCarRepository.saveAll(carsToSave);
        return savedCars.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // --- CRUD: UPDATE ---
    @Override
    public DtoAdminCar updateCar(Long id, DtoAdminCarIU updateDto) {
        Car existingCar = adminCarRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ID " + id + " ile araba modeli bulunamad?."));

        BeanUtils.copyProperties(updateDto, existingCar, "id");

        Car updatedCar = adminCarRepository.save(existingCar);
        return convertToDto(updatedCar);
    }

    // --- CRUD: DELETE ---
    @Override
    public void deleteCar(Long id) {
        if (!adminCarRepository.existsById(id)) {
            throw new NoSuchElementException("ID " + id + " ile araba modeli bulunamad??? i?in silinemedi.");
        }
        adminCarRepository.deleteById(id);
    }
}