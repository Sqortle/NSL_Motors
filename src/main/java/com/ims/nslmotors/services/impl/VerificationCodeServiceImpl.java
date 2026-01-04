package com.ims.nslmotors.services.impl;

import com.ims.nslmotors.model.Customer;
import com.ims.nslmotors.model.Employee;
import com.ims.nslmotors.model.VerificationCode;
import com.ims.nslmotors.repository.VerificationCodeRepository;
import com.ims.nslmotors.services.IVerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements IVerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;
    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRY_MINUTES = 10; // Kod 10 dakika geçerli

    @Override
    @Transactional
    public String generateAndSaveVerificationCode(Customer customer, String purpose) {
        // 6 haneli rastgele kod oluştur
        String code = generateRandomCode();

        // Yeni VerificationCode entity oluştur
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCustomer(customer);
        verificationCode.setCode(code);
        verificationCode.setIsUsed(false);
        verificationCode.setPurpose(purpose);
        verificationCode.setCreatedAt(LocalDateTime.now());
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));

        // Veritabanına kaydet
        verificationCodeRepository.save(verificationCode);

        return code;
    }

    @Override
    public boolean verifyCode(Long customerId, String code, String purpose) {
        LocalDateTime now = LocalDateTime.now();
        Optional<VerificationCode> verificationCodeOpt = verificationCodeRepository
                .findByCustomerIdAndCodeAndIsUsedFalseAndExpiresAtAfter(customerId, code, now);

        if (verificationCodeOpt.isEmpty()) {
            return false;
        }

        VerificationCode verificationCode = verificationCodeOpt.get();
        // Purpose kontrolü
        if (purpose != null && !purpose.equals(verificationCode.getPurpose())) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public void markCodeAsUsed(Long customerId, String code, String purpose) {
        LocalDateTime now = LocalDateTime.now();
        Optional<VerificationCode> verificationCodeOpt = verificationCodeRepository
                .findByCustomerIdAndCodeAndIsUsedFalseAndExpiresAtAfter(customerId, code, now);

        if (verificationCodeOpt.isPresent()) {
            VerificationCode verificationCode = verificationCodeOpt.get();
            if (purpose == null || purpose.equals(verificationCode.getPurpose())) {
                verificationCode.setIsUsed(true);
                verificationCodeRepository.save(verificationCode);
            }
        }
    }

    @Override
    @Transactional
    public String generateAndSaveVerificationCodeForEmployee(Employee employee, String purpose) {
        // 6 haneli rastgele kod oluştur
        String code = generateRandomCode();

        // Yeni VerificationCode entity oluştur
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmployee(employee);
        verificationCode.setCode(code);
        verificationCode.setIsUsed(false);
        verificationCode.setPurpose(purpose);
        verificationCode.setCreatedAt(LocalDateTime.now());
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));

        // Veritabanına kaydet
        verificationCodeRepository.save(verificationCode);

        return code;
    }

    @Override
    public boolean verifyCodeForEmployee(Long employeeId, String code, String purpose) {
        LocalDateTime now = LocalDateTime.now();
        Optional<VerificationCode> verificationCodeOpt = verificationCodeRepository
                .findByEmployeeIdAndCodeAndIsUsedFalseAndExpiresAtAfter(employeeId, code, now);

        if (verificationCodeOpt.isEmpty()) {
            return false;
        }

        VerificationCode verificationCode = verificationCodeOpt.get();
        // Purpose kontrolü
        if (purpose != null && !purpose.equals(verificationCode.getPurpose())) {
            return false;
        }

        return true;
    }

    @Override
    public boolean verifyCodeByEmail(String email, String code, String purpose) {
        LocalDateTime now = LocalDateTime.now();
        
        // Önce customer için kontrol et
        Optional<VerificationCode> customerCodeOpt = verificationCodeRepository
                .findByCustomerEmailAndCodeAndIsUsedFalseAndExpiresAtAfter(email, code, now);
        
        if (customerCodeOpt.isPresent()) {
            VerificationCode verificationCode = customerCodeOpt.get();
            if (purpose == null || purpose.equals(verificationCode.getPurpose())) {
                return true;
            }
        }
        
        // Sonra employee için kontrol et
        Optional<VerificationCode> employeeCodeOpt = verificationCodeRepository
                .findByEmployeeEmailAndCodeAndIsUsedFalseAndExpiresAtAfter(email, code, now);
        
        if (employeeCodeOpt.isPresent()) {
            VerificationCode verificationCode = employeeCodeOpt.get();
            if (purpose == null || purpose.equals(verificationCode.getPurpose())) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    @Transactional
    public void markCodeAsUsedForEmployee(Long employeeId, String code, String purpose) {
        LocalDateTime now = LocalDateTime.now();
        Optional<VerificationCode> verificationCodeOpt = verificationCodeRepository
                .findByEmployeeIdAndCodeAndIsUsedFalseAndExpiresAtAfter(employeeId, code, now);

        if (verificationCodeOpt.isPresent()) {
            VerificationCode verificationCode = verificationCodeOpt.get();
            if (purpose == null || purpose.equals(verificationCode.getPurpose())) {
                verificationCode.setIsUsed(true);
                verificationCodeRepository.save(verificationCode);
            }
        }
    }

    @Override
    @Transactional
    public void markCodeAsUsedByEmail(String email, String code, String purpose) {
        LocalDateTime now = LocalDateTime.now();
        
        // Önce customer için kontrol et
        Optional<VerificationCode> customerCodeOpt = verificationCodeRepository
                .findByCustomerEmailAndCodeAndIsUsedFalseAndExpiresAtAfter(email, code, now);
        
        if (customerCodeOpt.isPresent()) {
            VerificationCode verificationCode = customerCodeOpt.get();
            if (purpose == null || purpose.equals(verificationCode.getPurpose())) {
                verificationCode.setIsUsed(true);
                verificationCodeRepository.save(verificationCode);
                return;
            }
        }
        
        // Sonra employee için kontrol et
        Optional<VerificationCode> employeeCodeOpt = verificationCodeRepository
                .findByEmployeeEmailAndCodeAndIsUsedFalseAndExpiresAtAfter(email, code, now);
        
        if (employeeCodeOpt.isPresent()) {
            VerificationCode verificationCode = employeeCodeOpt.get();
            if (purpose == null || purpose.equals(verificationCode.getPurpose())) {
                verificationCode.setIsUsed(true);
                verificationCodeRepository.save(verificationCode);
            }
        }
    }

    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}

