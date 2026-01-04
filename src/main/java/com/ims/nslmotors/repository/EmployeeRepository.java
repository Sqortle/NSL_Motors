package com.ims.nslmotors.repository;

import com.ims.nslmotors.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    // Email ile çalışan bul
    Optional<Employee> findByEmail(String email);
    
    // Dükkan ve role göre çalışanları bul
    List<Employee> findByShopIdAndRoleIn(Long shopId, List<String> roles);
    
    // Role göre çalışanları bul
    List<Employee> findByRole(String role);
    
    // Dükkan'a göre tüm çalışanları bul
    List<Employee> findByShopId(Long shopId);
}
