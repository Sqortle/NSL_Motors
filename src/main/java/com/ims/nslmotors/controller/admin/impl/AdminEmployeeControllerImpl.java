package com.ims.nslmotors.controller.admin.impl;

import com.ims.nslmotors.controller.admin.IAdminEmployeeController;
import com.ims.nslmotors.dto.admin.DtoAdminEmployee;
import com.ims.nslmotors.dto.admin.DtoAdminEmployeeIU;
import com.ims.nslmotors.services.admin.IAdminEmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/employees") // Yolu /api/admin/customers olarak d?zenledik
@RequiredArgsConstructor
public class AdminEmployeeControllerImpl implements IAdminEmployeeController {
    @Autowired
    IAdminEmployeeService employeeService;


    @Override
    @GetMapping("/list")
    public ResponseEntity<Page<DtoAdminEmployee>> getEmployees(
            // Tüm arama filtrelerini alır (firstName, email, vb.)
            @ModelAttribute DtoAdminEmployee dtoAdminEmployee,
            // page, size, sort parametrelerini otomatik doldurur
            Pageable pageable) {

        Page<DtoAdminEmployee> employeePage = employeeService.getEmployeesWithPaginationAndSearch(dtoAdminEmployee, pageable);

        return ResponseEntity.ok(employeePage); // HTTP 200 OK
    }

    @Override
    @PostMapping("/add")
    public ResponseEntity<DtoAdminEmployee> createEmployee(@Valid @RequestBody DtoAdminEmployeeIU employeeCreationDto) {
        DtoAdminEmployee createdEmployee = employeeService.createEmployee(employeeCreationDto);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    @Override
    @PostMapping("/bulk")
    public ResponseEntity<List<DtoAdminEmployee>> createEmployeesBulk(@Valid @RequestBody List<DtoAdminEmployeeIU> employeeList) {
        List<DtoAdminEmployee> createdEmployees = employeeService.createEmployeesBulk(employeeList);
        return new ResponseEntity<>(createdEmployees, HttpStatus.CREATED);
    }

    @Override
    @PutMapping("update/{id}")
    public ResponseEntity<DtoAdminEmployee> updateEmployee(@PathVariable Long id, @Valid @RequestBody DtoAdminEmployeeIU updateDto) {
        DtoAdminEmployee updatedEmployee = employeeService.updateEmployee(id, updateDto);
        return ResponseEntity.ok(updatedEmployee);
    }

    @Override
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
