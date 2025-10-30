package com.ims.nslmotors.services.admin;


import com.ims.nslmotors.dto.admin.DtoAdminEmployee;
import com.ims.nslmotors.dto.admin.DtoAdminEmployeeIU;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdminEmployeeService {
    Page<DtoAdminEmployee> getEmployeesWithPaginationAndSearch(DtoAdminEmployee dtoAdminEmployee, Pageable pageable);

    DtoAdminEmployee createEmployee(DtoAdminEmployeeIU employeeCreationDto);

    List<DtoAdminEmployee> createEmployeesBulk(List<DtoAdminEmployeeIU> employeeCreationDtos);

    DtoAdminEmployee updateEmployee(Long id, DtoAdminEmployeeIU updateDto);

    void deleteEmployee(Long id);
}
