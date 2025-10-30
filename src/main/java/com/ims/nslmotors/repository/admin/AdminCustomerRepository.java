package com.ims.nslmotors.repository.admin;

import com.ims.nslmotors.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminCustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {


}
