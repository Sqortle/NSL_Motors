package com.ims.nslmotors.controller.admin;

import com.ims.nslmotors.dto.admin.DtoAdminEmployee;
import com.ims.nslmotors.dto.admin.DtoAdminEmployeeIU;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAdminEmployeeController {

    ResponseEntity<Page<DtoAdminEmployee>> getEmployees(DtoAdminEmployee dtoAdminEmployee, Pageable pageable, HttpSession session);

    ResponseEntity<DtoAdminEmployee> createEmployee(@Valid DtoAdminEmployeeIU employeeCreationDto,
                                                    HttpSession session);

    ResponseEntity<List<DtoAdminEmployee>> createEmployeesBulk(@Valid List<DtoAdminEmployeeIU> employeeList,
                                                               HttpSession session);

    ResponseEntity<DtoAdminEmployee> updateEmployee(Long id,
                                                    @Valid DtoAdminEmployeeIU updateDto,
                                                    HttpSession session);

    ResponseEntity<Void> deleteEmployee(Long id, HttpSession session);

}
