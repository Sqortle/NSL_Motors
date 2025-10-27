package com.ims.nslmotors.repository;

import com.ims.nslmotors.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // findAll() metodu JpaRepository'den otomatik olarak miras alındı.
}