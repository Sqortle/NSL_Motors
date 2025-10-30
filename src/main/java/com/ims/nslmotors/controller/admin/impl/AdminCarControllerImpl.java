package com.ims.nslmotors.controller.admin.impl;

import com.ims.nslmotors.controller.admin.IAdminCarController;
import com.ims.nslmotors.dto.admin.DtoAdminCar;
import com.ims.nslmotors.dto.admin.DtoAdminCarIU;
import com.ims.nslmotors.services.admin.IAdminCarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Sayfalama deste?i
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cars") // Ana yol: /api/admin/cars
@RequiredArgsConstructor
public class AdminCarControllerImpl implements IAdminCarController {

    private final IAdminCarService carService;

    // --- CRUD: READ (Sayfalandırma ve Esnek Arama) ---
    // URL: GET /api/admin/cars?page=0&size=10&make=Honda
    @Override
    @GetMapping("/list")
    public ResponseEntity<Page<DtoAdminCar>> getCars(
            // Filtreleme parametrelerini DtoCar nesnesine ba?lar
            @ModelAttribute DtoAdminCar criteria,
            // Spring, page, size, sort parametrelerini otomatik doldurur
            Pageable pageable) {

        Page<DtoAdminCar> carPage = carService.getCars(criteria, pageable);

        return ResponseEntity.ok(carPage);
    }

    // --- CRUD: CREATE (Tekil) ---
    // URL: POST /api/admin/cars
    @Override
    @PostMapping("/add")
    public ResponseEntity<DtoAdminCar> createCar(@Valid @RequestBody DtoAdminCarIU carCreationDto) {
        DtoAdminCar createdCar = carService.createCar(carCreationDto);
        // Ba?ar?l? olu?turma i?in HTTP 201 Created
        return new ResponseEntity<>(createdCar, HttpStatus.CREATED);
    }

    // --- CRUD: CREATE (Toplu) ---
    // URL: POST /api/admin/cars/bulk
    @Override
    @PostMapping("/bulk")
    public ResponseEntity<List<DtoAdminCar>> createCarsBulk(@Valid @RequestBody List<DtoAdminCarIU> carCreationDtos) {
        List<DtoAdminCar> createdList = carService.createCarsBulk(carCreationDtos);
        // Ba?ar?l? olu?turma i?in HTTP 201 Created
        return new ResponseEntity<>(createdList, HttpStatus.CREATED);
    }

    // --- CRUD: UPDATE ---
    // URL: PUT /api/admin/cars/{id}
    @Override
    @PutMapping("update/{id}")
    public ResponseEntity<DtoAdminCar> updateCar(
            @PathVariable Long id, // URL'deki ID'yi al
            @Valid @RequestBody DtoAdminCarIU updateDto) { // G?ncel veriyi Body'den al

        DtoAdminCar updatedCar = carService.updateCar(id, updateDto);
        // Ba?ar?l? g?ncelleme i?in HTTP 200 OK
        return ResponseEntity.ok(updatedCar);
    }

    // --- CRUD: DELETE ---
    // URL: DELETE /api/admin/cars/{id}
    @Override
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {

        carService.deleteCar(id);
        // Ba?ar?l? silme i?lemi i?in HTTP 204 No Content
        return ResponseEntity.noContent().build();
    }
}