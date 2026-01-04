package com.ims.nslmotors.repository;

import com.ims.nslmotors.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findByCustomerIdAndCodeAndIsUsedFalseAndExpiresAtAfter(
            Long customerId, String code, LocalDateTime now);

    Optional<VerificationCode> findFirstByCustomerIdAndPurposeAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            Long customerId, String purpose, LocalDateTime now);

    // Employee için metodlar
    Optional<VerificationCode> findByEmployeeIdAndCodeAndIsUsedFalseAndExpiresAtAfter(
            Long employeeId, String code, LocalDateTime now);

    Optional<VerificationCode> findFirstByEmployeeIdAndPurposeAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            Long employeeId, String purpose, LocalDateTime now);

    // Email bazlı metodlar (hem customer hem employee için)
    @Query("SELECT vc FROM VerificationCode vc WHERE vc.customer.email = :email AND vc.code = :code AND vc.isUsed = false AND vc.expiresAt > :now")
    Optional<VerificationCode> findByCustomerEmailAndCodeAndIsUsedFalseAndExpiresAtAfter(
            @Param("email") String email, @Param("code") String code, @Param("now") LocalDateTime now);

    @Query("SELECT vc FROM VerificationCode vc WHERE vc.employee.email = :email AND vc.code = :code AND vc.isUsed = false AND vc.expiresAt > :now")
    Optional<VerificationCode> findByEmployeeEmailAndCodeAndIsUsedFalseAndExpiresAtAfter(
            @Param("email") String email, @Param("code") String code, @Param("now") LocalDateTime now);
    
    // Payment verification
    java.util.List<VerificationCode> findByCodeStartingWithAndIsUsedFalseAndExpiresAtAfter(
            String code, LocalDateTime now);
    
    @Query("SELECT vc FROM VerificationCode vc WHERE vc.code = :code AND vc.appointment.id = :appointmentId AND vc.isUsed = false AND vc.expiresAt > :now AND vc.purpose = 'PAYMENT'")
    Optional<VerificationCode> findByCodeAndAppointmentIdAndIsUsedFalseAndExpiresAtAfter(
            @Param("code") String code, @Param("appointmentId") Long appointmentId, @Param("now") LocalDateTime now);
}

