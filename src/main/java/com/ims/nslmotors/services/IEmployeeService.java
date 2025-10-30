package com.ims.nslmotors.services;


import com.ims.nslmotors.dto.DtoEmployee;
import com.ims.nslmotors.dto.DtoEmployeeIU;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IEmployeeService {
    Page<DtoEmployee> getEmployeesWithPaginationAndSearch(DtoEmployee dtoEmployee, Pageable pageable);

    DtoEmployee createEmployee(DtoEmployeeIU employeeCreationDto);

    List<DtoEmployee> createEmployeesBulk(List<DtoEmployeeIU> employeeCreationDtos);

    DtoEmployee updateEmployee(Long id, DtoEmployeeIU updateDto);

    void deleteEmployee(Long id);
}
