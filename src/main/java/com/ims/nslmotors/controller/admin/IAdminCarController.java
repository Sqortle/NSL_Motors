package com.ims.nslmotors.controller.admin;

import com.ims.nslmotors.dto.admin.DtoAdminCar;
import com.ims.nslmotors.dto.admin.DtoAdminCarIU;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

import java.util.List;

public interface IAdminCarController {

    // CRUD: READ
    ResponseEntity<Page<DtoAdminCar>> getCars(DtoAdminCar criteria, Pageable pageable);

    // CRUD: CREATE
    ResponseEntity<DtoAdminCar> createCar(@Valid DtoAdminCarIU carCreationDto);
    ResponseEntity<List<DtoAdminCar>> createCarsBulk(@Valid List<DtoAdminCarIU> carCreationDtos);

    // CRUD: UPDATE
    ResponseEntity<DtoAdminCar> updateCar(Long id, @Valid DtoAdminCarIU updateDto);

    // CRUD: DELETE
    ResponseEntity<Void> deleteCar(Long id);
}