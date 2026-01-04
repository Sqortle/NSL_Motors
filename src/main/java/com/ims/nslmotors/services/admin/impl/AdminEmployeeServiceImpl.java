package com.ims.nslmotors.services.admin.impl;

import com.ims.nslmotors.dto.admin.DtoAdminEmployee;
import com.ims.nslmotors.dto.admin.DtoAdminEmployeeIU;
import com.ims.nslmotors.model.Employee;
import com.ims.nslmotors.repository.admin.AdminEmployeeRepository;
import com.ims.nslmotors.services.admin.IAdminEmployeeService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class AdminEmployeeServiceImpl implements IAdminEmployeeService {

    @Autowired
    private AdminEmployeeRepository adminEmployeeRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Page<DtoAdminEmployee> getEmployeesWithPaginationAndSearch(DtoAdminEmployee dtoAdminEmployee, Pageable pageable, String currentUserRole) {
        Specification<Employee> specification = buildSpecification(dtoAdminEmployee, currentUserRole);

        Page<Employee> customerPage = adminEmployeeRepository.findAll(specification, pageable);

        return customerPage.map(this::convertToDto);
    }

    private Specification<Employee> buildSpecification(DtoAdminEmployee dtoAdminEmployee, String currentUserRole) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dtoAdminEmployee.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), dtoAdminEmployee.getId()));
            }

            if (dtoAdminEmployee.getFirstName() != null && !dtoAdminEmployee.getFirstName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                        "%" + dtoAdminEmployee.getFirstName().toLowerCase() + "%"));
            }
            if (dtoAdminEmployee.getLastName() != null && !dtoAdminEmployee.getLastName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
                        "%" + dtoAdminEmployee.getLastName().toLowerCase() + "%"));
            }
            if (dtoAdminEmployee.getEmail() != null && !dtoAdminEmployee.getEmail().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                        "%" + dtoAdminEmployee.getEmail().toLowerCase() + "%"));
            }
            if (dtoAdminEmployee.getPhoneNumber() != null && !dtoAdminEmployee.getPhoneNumber().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("phoneNumber"),
                        "%" + dtoAdminEmployee.getPhoneNumber() + "%"));
            }
            if (dtoAdminEmployee.getShopName() != null && !dtoAdminEmployee.getShopName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("shopName"),
                        "%" + dtoAdminEmployee.getShopName() + "%"));
            }
            if (dtoAdminEmployee.getAddress() != null && !dtoAdminEmployee.getAddress().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("address"),
                        "%" + dtoAdminEmployee.getAddress() + "%"));
            }
            if (dtoAdminEmployee.getTcKimlikNo() != null && !dtoAdminEmployee.getTcKimlikNo().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("tcKimlikNo"),
                        "%" + dtoAdminEmployee.getTcKimlikNo() + "%"));
            }
            // Role filtresi
            if (dtoAdminEmployee.getRole() != null && !dtoAdminEmployee.getRole().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("role"), dtoAdminEmployee.getRole()));
            }

            // KRİTİK GÜVENLİK: OWNER rolündeki kullanıcıları sadece OWNER'lar görebilir
            // Eğer giriş yapan kullanıcı OWNER değilse, OWNER rolündeki kullanıcıları filtrele
            if (!"OWNER".equals(currentUserRole)) {
                predicates.add(criteriaBuilder.notEqual(root.get("role"), "OWNER"));
            }

            // Tüm koşulları AND ile birleştir
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public DtoAdminEmployee createEmployee(DtoAdminEmployeeIU employeeCreationDto) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeCreationDto, employee);
        
        // KRİTİK DÜZELTME: Yeni entity oluştururken ID'yi null yaparak 
        // Hibernate'in merge yerine persist kullanmasını sağlıyoruz
        employee.setId(null);
        
        // KRİTİK GÜVENLİK: Şifreyi BCrypt ile hashle
        if (employeeCreationDto.getPassword() != null && !employeeCreationDto.getPassword().trim().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(employeeCreationDto.getPassword()));
        }
        
        Employee savedEmployee = adminEmployeeRepository.save(employee);
        return convertToDto(savedEmployee);
    }

    @Override
    public List<DtoAdminEmployee> createEmployeesBulk(List<DtoAdminEmployeeIU> employeeCreationDtos) {
        List<Employee> employeesToSave = employeeCreationDtos.stream()
                .map(dto -> {
                    Employee employee = new Employee();
                    // DTO'dan Entity'ye kopyalama
                    BeanUtils.copyProperties(dto, employee);
                    // KRİTİK DÜZELTME: Yeni entity oluştururken ID'yi null yaparak 
                    // Hibernate'in merge yerine persist kullanmasını sağlıyoruz
                    employee.setId(null);
                    // Şifreyi BCrypt ile hashle
                    if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                        employee.setPassword(passwordEncoder.encode(dto.getPassword()));
                    }
                    return employee;
                })
                .collect(Collectors.toList());

        // 2. Entity Listesini JPA'nın saveAll metodu ile toplu kaydet
        List<Employee> savedEmployees = adminEmployeeRepository.saveAll(employeesToSave);

        // 3. Kaydedilen Entity Listesini Response DTO Listesine dönüştür
        return savedEmployees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DtoAdminEmployee updateEmployee(Long id, DtoAdminEmployeeIU updateDto) {
        Employee existingEmployee = adminEmployeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ID " + id + " ile çalışan bulunamadı."));
        
        String oldPassword = existingEmployee.getPassword();
        BeanUtils.copyProperties(updateDto, existingEmployee);
        
        // ID'yi koru
        existingEmployee.setId(id);

        // Şifre güncelleme kontrolü
        if (updateDto.getPassword() == null || updateDto.getPassword().trim().isEmpty()) {
            existingEmployee.setPassword(oldPassword);
        } else {
            // Yeni şifreyi BCrypt ile hashle
            existingEmployee.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        Employee updatedEmployee = adminEmployeeRepository.save(existingEmployee);
        return convertToDto(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!adminEmployeeRepository.existsById(id)) {
            // Var olmayan bir ID silinmeye çalışılırsa hata fırlat
            throw new NoSuchElementException("ID " + id + " ile müşteri bulunamadığı için silinemedi.");
        }

        // 2. Silme işlemini gerçekleştir
        // NOT: Customer Entity'nizde Order Entity'sine olan ilişki (cascade = CascadeType.ALL, orphanRemoval = true)
        // olduğu için, bu müşteriye ait tüm siparişler de otomatik olarak silinecektir.
        adminEmployeeRepository.deleteById(id);
    }

    private DtoAdminEmployee convertToDto(Employee employee) {
        DtoAdminEmployee dto = new DtoAdminEmployee();
        BeanUtils.copyProperties(employee, dto);
        return dto;
    }
}
