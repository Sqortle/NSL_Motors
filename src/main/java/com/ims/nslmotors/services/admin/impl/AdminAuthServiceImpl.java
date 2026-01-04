package com.ims.nslmotors.services.admin.impl;

import com.ims.nslmotors.dto.admin.DtoAdminLogin;
import com.ims.nslmotors.model.Employee;
import com.ims.nslmotors.repository.admin.AdminEmployeeRepository;
import com.ims.nslmotors.services.IMailService;
import com.ims.nslmotors.services.IVerificationCodeService;
import com.ims.nslmotors.services.admin.IAdminAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements IAdminAuthService {

    private final AdminEmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final IVerificationCodeService verificationCodeService;
    private final IMailService mailService;

    @Override
    @Transactional
    public void initiateLogin(DtoAdminLogin loginDto) {
        log.debug("Admin giriş denemesi - Email: {}", loginDto.getEmail());
        
        // 1. Employee'yi email ile bul
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(loginDto.getEmail());
        if (employeeOpt.isEmpty()) {
            log.warn("Admin giriş hatası - Email bulunamadı: {}", loginDto.getEmail());
            throw new RuntimeException("Email veya şifre hatalı.");
        }

        Employee employee = employeeOpt.get();
        log.debug("Employee bulundu - ID: {}, Role: {}, Email: {}", employee.getId(), employee.getRole(), employee.getEmail());

        // 2. Şifreyi kontrol et
        String storedPassword = employee.getPassword();
        log.debug("Şifre kontrolü başlatılıyor - Hash'lenmiş mi: {}", 
                storedPassword != null && (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")));
        
        // Eğer şifre hash'lenmemişse (BCrypt hash'i $2a$ veya $2b$ ile başlar)
        if (storedPassword != null && !storedPassword.startsWith("$2a$") && !storedPassword.startsWith("$2b$")) {
            log.debug("Şifre hash'lenmemiş, düz metin kontrolü yapılıyor");
            // Şifre hash'lenmemiş (düz metin), girilen şifreyle karşılaştır
            if (!storedPassword.equals(loginDto.getPassword())) {
                log.warn("Admin giriş hatası - Şifre eşleşmedi (düz metin)");
                throw new RuntimeException("Email veya şifre hatalı.");
            }
            // Şifre doğru, hash'le ve kaydet
            log.info("Şifre doğru, hash'leniyor ve kaydediliyor - Email: {}", loginDto.getEmail());
            String hashedPassword = passwordEncoder.encode(loginDto.getPassword());
            employee.setPassword(hashedPassword);
            employeeRepository.save(employee);
            log.info("Şifre başarıyla hash'lendi ve kaydedildi");
        } else {
            // Şifre zaten hash'lenmiş, BCrypt ile kontrol et
            log.debug("Şifre hash'lenmiş, BCrypt kontrolü yapılıyor");
            log.debug("Girilen şifre uzunluğu: {}, Hash uzunluğu: {}", 
                    loginDto.getPassword() != null ? loginDto.getPassword().length() : 0,
                    storedPassword != null ? storedPassword.length() : 0);
            log.debug("Hash başlangıcı: {}", storedPassword != null && storedPassword.length() > 10 ? storedPassword.substring(0, 10) : "null");
            
            if (!passwordEncoder.matches(loginDto.getPassword(), storedPassword)) {
                log.warn("Admin giriş hatası - Şifre eşleşmedi (hash'lenmiş)");
                log.warn("Girilen şifre: '{}', Hash: '{}'", loginDto.getPassword(), storedPassword != null ? storedPassword.substring(0, Math.min(20, storedPassword.length())) : "null");
                throw new RuntimeException("Email veya şifre hatalı. Lütfen şifrenizi kontrol edin.");
            }
            log.debug("Şifre doğru, giriş başarılı");
        }
        
        // 4. Doğrulama kodu oluştur ve gönder
        try {
            // Purpose'i role göre belirle
            String role = employee.getRole();
            boolean isAdminLike = "ADMIN".equals(role) || "OWNER".equals(role);
            String purpose = isAdminLike ? "ADMIN_LOGIN" : "EMPLOYEE_LOGIN";
            String verificationCode = verificationCodeService.generateAndSaveVerificationCodeForEmployee(
                    employee, purpose);
            mailService.sendVerificationCode(employee.getEmail(), verificationCode, purpose);
            log.info("Employee doğrulama kodu gönderildi - Email: {}, Role: {}", loginDto.getEmail(), employee.getRole());
        } catch (RuntimeException e) {
            log.error("Doğrulama kodu gönderilemedi - Email: {}, Hata: {}", loginDto.getEmail(), e.getMessage());
            throw new RuntimeException("Doğrulama kodu email adresinize gönderilemedi. Lütfen mail ayarlarını kontrol edin veya yönetici ile iletişime geçin. Hata: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void verifyLogin(String email, String verificationCode) {
        log.debug("Admin doğrulama kodu kontrolü - Email: {}", email);
        
        // 1. Employee'yi email ile bul
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(email);
        if (employeeOpt.isEmpty()) {
            log.warn("Admin doğrulama hatası - Email bulunamadı: {}", email);
            throw new RuntimeException("Email bulunamadı.");
        }

        Employee employee = employeeOpt.get();
        
        // 2. Purpose'i role göre belirle ve doğrulama kodunu kontrol et
        String role = employee.getRole();
        boolean isAdminLike = "ADMIN".equals(role) || "OWNER".equals(role);
        String purpose = isAdminLike ? "ADMIN_LOGIN" : "EMPLOYEE_LOGIN";
        boolean isValid = verificationCodeService.verifyCodeForEmployee(
                employee.getId(), verificationCode, purpose);

        if (!isValid) {
            log.warn("Admin doğrulama hatası - Geçersiz kod. Email: {}", email);
            throw new RuntimeException("Geçersiz veya süresi dolmuş doğrulama kodu.");
        }

        // 3. Kodu kullanılmış olarak işaretle
        verificationCodeService.markCodeAsUsedForEmployee(employee.getId(), verificationCode, purpose);
        
        log.info("Employee doğrulama başarılı - Email: {}, Role: {}", email, employee.getRole());
    }
}

