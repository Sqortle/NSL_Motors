package com.ims.nslmotors.services.impl;

import com.ims.nslmotors.dto.DtoEmployee;
import com.ims.nslmotors.dto.DtoEmployeeIU;
import com.ims.nslmotors.model.Customer;
import com.ims.nslmotors.model.Employee;
import com.ims.nslmotors.repository.EmployeeRepository;
import com.ims.nslmotors.services.IEmployeeService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Page<DtoEmployee> getEmployeesWithPaginationAndSearch(DtoEmployee dtoEmployee, Pageable pageable) {
        Specification<Employee> specification = buildSpecification(dtoEmployee);

        Page<Employee> customerPage = employeeRepository.findAll(specification, pageable);

        return customerPage.map(this::convertToDto);
    }

    private Specification<Employee> buildSpecification(DtoEmployee dtoEmployee) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dtoEmployee.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), dtoEmployee.getId()));
            }

            if (dtoEmployee.getFirstName() != null && !dtoEmployee.getFirstName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                        "%" + dtoEmployee.getFirstName().toLowerCase() + "%"));
            }
            if (dtoEmployee.getLastName() != null && !dtoEmployee.getLastName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
                        "%" + dtoEmployee.getLastName().toLowerCase() + "%"));
            }
            if (dtoEmployee.getEmail() != null && !dtoEmployee.getEmail().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                        "%" + dtoEmployee.getEmail().toLowerCase() + "%"));
            }
            if (dtoEmployee.getPhoneNumber() != null && !dtoEmployee.getPhoneNumber().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("phoneNumber"),
                        "%" + dtoEmployee.getPhoneNumber() + "%"));
            }
            if (dtoEmployee.getShopName() != null && !dtoEmployee.getShopName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("shopName"),
                        "%" + dtoEmployee.getShopName() + "%"));
            }
            if (dtoEmployee.getAddress() != null && !dtoEmployee.getAddress().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("address"),
                        "%" + dtoEmployee.getAddress() + "%"));
            }
            if (dtoEmployee.getTcKimlikNo() != null && !dtoEmployee.getTcKimlikNo().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("tcKimlikNo"),
                        "%" + dtoEmployee.getTcKimlikNo() + "%"));
            }

            // T?m ko?ullar? AND ile birle?tir
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public DtoEmployee createEmployee(DtoEmployeeIU employeeCreationDto) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeCreationDto, employee);
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDto(savedEmployee);
    }

    @Override
    public List<DtoEmployee> createEmployeesBulk(List<DtoEmployeeIU> employeeCreationDtos) {
        List<Employee> employeesToSave = employeeCreationDtos.stream()
                .map(dto -> {
                    Employee employee = new Employee();
                    // DTO'dan Entity'ye kopyalama
                    BeanUtils.copyProperties(dto, employee);
                    // Not: Şifre hash'leme işlemi burada yapılmalıdır (Security aktif olunca).
                    return employee;
                })
                .collect(Collectors.toList());

        // 2. Entity Listesini JPA'nın saveAll metodu ile toplu kaydet
        List<Employee> savedEmployees = employeeRepository.saveAll(employeesToSave);

        // 3. Kaydedilen Entity Listesini Response DTO Listesine dönüştür
        return savedEmployees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DtoEmployee updateEmployee(Long id, DtoEmployeeIU updateDto) {
        Employee existingEmployee = employeeRepository.findById(id).orElse(null);
        String oldPassword = existingEmployee.getPassword();

        BeanUtils.copyProperties(updateDto, existingEmployee);

        if (updateDto.getPassword() == null || updateDto.getPassword().trim().isEmpty()) {
            existingEmployee.setPassword(oldPassword);
        }

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return convertToDto(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            // Var olmayan bir ID silinmeye çalışılırsa hata fırlat
            throw new NoSuchElementException("ID " + id + " ile müşteri bulunamadığı için silinemedi.");
        }

        // 2. Silme işlemini gerçekleştir
        // NOT: Customer Entity'nizde Order Entity'sine olan ilişki (cascade = CascadeType.ALL, orphanRemoval = true)
        // olduğu için, bu müşteriye ait tüm siparişler de otomatik olarak silinecektir.
        employeeRepository.deleteById(id);
    }

    private DtoEmployee convertToDto(Employee employee) {
        DtoEmployee dto = new DtoEmployee();
        BeanUtils.copyProperties(employee, dto);
        return dto;
    }
}
