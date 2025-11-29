package com.ims.nslmotors.repository;

import com.ims.nslmotors.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findByCustomerIdAndCodeAndIsUsedFalseAndExpiresAtAfter(
            Long customerId, String code, LocalDateTime now);

    Optional<VerificationCode> findFirstByCustomerIdAndPurposeAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            Long customerId, String purpose, LocalDateTime now);
}

