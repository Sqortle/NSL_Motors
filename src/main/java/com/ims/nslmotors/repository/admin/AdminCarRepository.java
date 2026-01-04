package com.ims.nslmotors.repository.admin;

import com.ims.nslmotors.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Eklendi
import org.springframework.stereotype.Repository;

@Repository
public interface AdminCarRepository extends JpaRepository<Car, Long>,
        JpaSpecificationExecutor<Car> { // KRİTİK: Eklendi

    java.util.List<Car> findByMake(String make);
    
    void deleteByMakeIn(java.util.List<String> makes);
}