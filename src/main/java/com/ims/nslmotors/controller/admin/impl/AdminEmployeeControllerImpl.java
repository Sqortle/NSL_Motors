package com.ims.nslmotors.controller.admin.impl;

import com.ims.nslmotors.controller.admin.IAdminEmployeeController;
import com.ims.nslmotors.dto.admin.DtoAdminEmployee;
import com.ims.nslmotors.dto.admin.DtoAdminEmployeeIU;
import com.ims.nslmotors.services.admin.IAdminEmployeeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/employees") // Yolu /api/admin/employees
@RequiredArgsConstructor
public class AdminEmployeeControllerImpl implements IAdminEmployeeController {
    @Autowired
    IAdminEmployeeService employeeService;

    private boolean isOwnerOrMaster(HttpSession session) {
        Boolean isMasterUser = (Boolean) session.getAttribute("isMasterUser");
        String role = (String) session.getAttribute("employeeRole");
        return Boolean.TRUE.equals(isMasterUser) || "OWNER".equals(role);
    }


    @Override
    @GetMapping("/list")
    public ResponseEntity<Page<DtoAdminEmployee>> getEmployees(
            // Tüm arama filtrelerini alır (firstName, email, vb.)
            @ModelAttribute DtoAdminEmployee dtoAdminEmployee,
            // page, size, sort parametrelerini otomatik doldurur
            Pageable pageable,
            HttpSession session) {

        // Eğer sort parametresi yoksa, default olarak önce Role'e göre, sonra Ad'a göre A-Z sırala (case-insensitive)
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                Sort.by(Sort.Order.asc("role").ignoreCase(), Sort.Order.asc("firstName").ignoreCase()));
        }

        // Session'dan giriş yapan kullanıcının rolünü al
        String currentUserRole = (String) session.getAttribute("employeeRole");
        
        Page<DtoAdminEmployee> employeePage = employeeService.getEmployeesWithPaginationAndSearch(dtoAdminEmployee, pageable, currentUserRole);

        return ResponseEntity.ok(employeePage); // HTTP 200 OK
    }

    @Override
    @PostMapping("/add")
    public ResponseEntity<DtoAdminEmployee> createEmployee(@Valid @RequestBody DtoAdminEmployeeIU employeeCreationDto,
                                                          HttpSession session) {
        // Sadece OWNER (ve master) OWNER ve ADMIN rolü atayabilir
        String targetRole = employeeCreationDto.getRole();
        if (("OWNER".equals(targetRole) || "ADMIN".equals(targetRole)) && !isOwnerOrMaster(session)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        DtoAdminEmployee createdEmployee = employeeService.createEmployee(employeeCreationDto);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    @Override
    @PostMapping("/bulk")
    public ResponseEntity<List<DtoAdminEmployee>> createEmployeesBulk(@Valid @RequestBody List<DtoAdminEmployeeIU> employeeList,
                                                                      HttpSession session) {
        // Sadece OWNER (ve master) OWNER ve ADMIN rolü atayabilir
        boolean hasRestrictedRole = employeeList.stream()
                .anyMatch(dto -> "OWNER".equals(dto.getRole()) || "ADMIN".equals(dto.getRole()));
        if (hasRestrictedRole && !isOwnerOrMaster(session)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<DtoAdminEmployee> createdEmployees = employeeService.createEmployeesBulk(employeeList);
        return new ResponseEntity<>(createdEmployees, HttpStatus.CREATED);
    }

    @Override
    @PutMapping("update/{id}")
    public ResponseEntity<DtoAdminEmployee> updateEmployee(@PathVariable Long id,
                                                           @Valid @RequestBody DtoAdminEmployeeIU updateDto,
                                                           HttpSession session) {
        // Sadece OWNER (ve master) OWNER ve ADMIN rolü atayabilir
        String targetRole = updateDto.getRole();
        if (("OWNER".equals(targetRole) || "ADMIN".equals(targetRole)) && !isOwnerOrMaster(session)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        DtoAdminEmployee updatedEmployee = employeeService.updateEmployee(id, updateDto);
        return ResponseEntity.ok(updatedEmployee);
    }

    @Override
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id, HttpSession session) {
        // Çalışan silme işlemi tüm admin kullanıcılarına açık
        // (İsterseniz burada da rol kontrolü ekleyebilirsiniz)
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
