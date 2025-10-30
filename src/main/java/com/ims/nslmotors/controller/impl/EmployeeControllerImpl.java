package com.ims.nslmotors.controller.impl;

import com.ims.nslmotors.controller.IEmployeeController;
import com.ims.nslmotors.dto.DtoEmployee;
import com.ims.nslmotors.dto.DtoEmployeeIU;
import com.ims.nslmotors.services.IEmployeeService;
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
public class EmployeeControllerImpl implements IEmployeeController {
    @Autowired
    IEmployeeService employeeService;


    @Override
    @GetMapping("/list")
    public ResponseEntity<Page<DtoEmployee>> getEmployees(
            // Tüm arama filtrelerini alır (firstName, email, vb.)
            @ModelAttribute DtoEmployee dtoEmployee,
            // page, size, sort parametrelerini otomatik doldurur
            Pageable pageable) {

        Page<DtoEmployee> employeePage = employeeService.getEmployeesWithPaginationAndSearch(dtoEmployee, pageable);

        return ResponseEntity.ok(employeePage); // HTTP 200 OK
    }

    @Override
    @PostMapping("/add")
    public ResponseEntity<DtoEmployee> createEmployee(@Valid @RequestBody DtoEmployeeIU employeeCreationDto) {
        DtoEmployee  createdEmployee = employeeService.createEmployee(employeeCreationDto);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    @Override
    @PostMapping("/bulk")
    public ResponseEntity<List<DtoEmployee>> createEmployeesBulk(@Valid @RequestBody List<DtoEmployeeIU> employeeList) {
        List<DtoEmployee> createdEmployees = employeeService.createEmployeesBulk(employeeList);
        return new ResponseEntity<>(createdEmployees, HttpStatus.CREATED);
    }

    @Override
    @PutMapping("update/{id}")
    public ResponseEntity<DtoEmployee> updateEmployee(@PathVariable Long id, @Valid @RequestBody DtoEmployeeIU updateDto) {
        DtoEmployee  updatedEmployee = employeeService.updateEmployee(id, updateDto);
        return ResponseEntity.ok(updatedEmployee);
    }

    @Override
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
