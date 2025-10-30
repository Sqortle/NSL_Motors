package com.ims.nslmotors.controller;

import com.ims.nslmotors.dto.DtoEmployee;
import com.ims.nslmotors.dto.DtoEmployeeIU;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IEmployeeController {

    ResponseEntity<Page<DtoEmployee>> getEmployees(DtoEmployee dtoEmployee, Pageable pageable);

    ResponseEntity<DtoEmployee> createEmployee(@Valid DtoEmployeeIU employeeCreationDto);

    ResponseEntity<List<DtoEmployee>> createEmployeesBulk(@Valid List<DtoEmployeeIU> employeeList);

    ResponseEntity<DtoEmployee> updateEmployee(Long id, @Valid DtoEmployeeIU updateDto);

    ResponseEntity<Void> deleteEmployee(Long id);

}
