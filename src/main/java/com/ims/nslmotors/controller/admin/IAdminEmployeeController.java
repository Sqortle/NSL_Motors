package com.ims.nslmotors.controller.admin;

import com.ims.nslmotors.dto.admin.DtoAdminEmployee;
import com.ims.nslmotors.dto.admin.DtoAdminEmployeeIU;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAdminEmployeeController {

    ResponseEntity<Page<DtoAdminEmployee>> getEmployees(DtoAdminEmployee dtoAdminEmployee, Pageable pageable);

    ResponseEntity<DtoAdminEmployee> createEmployee(@Valid DtoAdminEmployeeIU employeeCreationDto);

    ResponseEntity<List<DtoAdminEmployee>> createEmployeesBulk(@Valid List<DtoAdminEmployeeIU> employeeList);

    ResponseEntity<DtoAdminEmployee> updateEmployee(Long id, @Valid DtoAdminEmployeeIU updateDto);

    ResponseEntity<Void> deleteEmployee(Long id);

}
