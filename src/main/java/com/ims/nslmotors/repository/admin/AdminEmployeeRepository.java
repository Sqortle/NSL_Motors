package com.ims.nslmotors.repository.admin;

import com.ims.nslmotors.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminEmployeeRepository extends JpaRepository<Employee,Long>, JpaSpecificationExecutor<Employee> {

}
