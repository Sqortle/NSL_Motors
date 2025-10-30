package com.ims.nslmotors.services.admin;

import com.ims.nslmotors.dto.admin.DtoAdminCar;
import com.ims.nslmotors.dto.admin.DtoAdminCarIU;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Eklendi

import java.util.List;

public interface IAdminCarService {

    // CRUD: READ - Sayfalandırma ve Esnek Arama (Yeni Metot)
    Page<DtoAdminCar> getCars(DtoAdminCar criteria, Pageable pageable);

    // CRUD: CREATE - Tekil
    DtoAdminCar createCar(DtoAdminCarIU carCreationDto);

    // CRUD: CREATE - Toplu
    List<DtoAdminCar> createCarsBulk(List<DtoAdminCarIU> carCreationDtos);

    // CRUD: UPDATE
    DtoAdminCar updateCar(Long id, DtoAdminCarIU updateDto);

    // CRUD: DELETE
    void deleteCar(Long id);
}