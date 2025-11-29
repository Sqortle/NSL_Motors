// CustomerAuthServiceImpl.java
package com.ims.nslmotors.services.customer.impl;

import com.ims.nslmotors.dto.customer.DtoCustomerProfile;
import com.ims.nslmotors.dto.customer.DtoCustomerRegistirationIU;
import com.ims.nslmotors.dto.customer.DtoCustomerLogin;
import com.ims.nslmotors.dto.customer.DtoCustomerVerification;
import com.ims.nslmotors.model.Customer;
import com.ims.nslmotors.repository.admin.AdminCustomerRepository;
import com.ims.nslmotors.services.IMailService;
import com.ims.nslmotors.services.IVerificationCodeService;
import com.ims.nslmotors.services.customer.ICustomerAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerAuthServiceImpl implements ICustomerAuthService {

    private final AdminCustomerRepository customerRepository;
    private final IMailService mailService;
    private final IVerificationCodeService verificationCodeService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void initiateRegistration(DtoCustomerRegistirationIU registrationDto) {
        // 1. Email'in zaten kayıtlı olup olmadığını kontrol et
        Optional<Customer> existingCustomer = customerRepository.findByEmail(registrationDto.getEmail());
        if (existingCustomer.isPresent()) {
            throw new RuntimeException("Bu email adresi zaten kayıtlı.");
        }

        // 2. Yeni müşteri oluştur
        Customer customer = new Customer();
        customer.setFirstName(registrationDto.getFirstName());
        customer.setLastName(registrationDto.getLastName());
        customer.setEmail(registrationDto.getEmail());
        customer.setPhoneNumber(registrationDto.getPhoneNumber());
        // Şifreyi BCrypt ile hashle
        customer.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        // 3. Müşteriyi kaydet
        Customer savedCustomer = customerRepository.save(customer);

        // 4. Doğrulama kodu oluştur ve gönder
        try {
            String verificationCode = verificationCodeService.generateAndSaveVerificationCode(
                    savedCustomer, "REGISTRATION");
            mailService.sendVerificationCode(savedCustomer.getEmail(), verificationCode, "REGISTRATION");
        } catch (RuntimeException e) {
            // Mail gönderme hatası olsa bile kayıt işlemi başarılı oldu
            // Kullanıcıya bilgi verelim ama kayıt işlemini iptal etmeyelim
            throw new RuntimeException("Kayıt işlemi tamamlandı ancak doğrulama kodu email adresinize gönderilemedi. Lütfen yönetici ile iletişime geçin. Hata: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public DtoCustomerProfile verifyAndActivateAccount(String email, String verificationCode) {
        // 1. Müşteriyi bul
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Müşteri bulunamadı."));

        // 2. Doğrulama kodunu kontrol et
        boolean isValid = verificationCodeService.verifyCode(
                customer.getId(), verificationCode, "REGISTRATION");

        if (!isValid) {
            throw new RuntimeException("Geçersiz veya süresi dolmuş doğrulama kodu.");
        }

        // 3. Kodu kullanılmış olarak işaretle
        verificationCodeService.markCodeAsUsed(customer.getId(), verificationCode, "REGISTRATION");

        // 4. DTO'ya dönüştür ve döndür
        DtoCustomerProfile profile = new DtoCustomerProfile();
        BeanUtils.copyProperties(customer, profile);
        return profile;
    }

    @Override
    @Transactional
    public void initiateLogin(DtoCustomerLogin loginDto) {
        // 1. Müşteriyi email ile bul
        Customer customer = customerRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email veya şifre hatalı."));

        // 2. Şifreyi kontrol et
        if (!passwordEncoder.matches(loginDto.getPassword(), customer.getPassword())) {
            throw new RuntimeException("Email veya şifre hatalı.");
        }

        // 3. Doğrulama kodu oluştur ve gönder
        try {
            String verificationCode = verificationCodeService.generateAndSaveVerificationCode(
                    customer, "LOGIN");
            mailService.sendVerificationCode(customer.getEmail(), verificationCode, "LOGIN");
        } catch (RuntimeException e) {
            throw new RuntimeException("Doğrulama kodu email adresinize gönderilemedi. Lütfen mail ayarlarını kontrol edin veya yönetici ile iletişime geçin. Hata: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public DtoCustomerProfile verifyLogin(DtoCustomerVerification verificationDto) {
        // 1. Müşteriyi bul
        Customer customer = customerRepository.findByEmail(verificationDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Müşteri bulunamadı."));

        // 2. Doğrulama kodunu kontrol et
        boolean isValid = verificationCodeService.verifyCode(
                customer.getId(), verificationDto.getVerificationCode(), "LOGIN");

        if (!isValid) {
            throw new RuntimeException("Geçersiz veya süresi dolmuş doğrulama kodu.");
        }

        // 3. Kodu kullanılmış olarak işaretle
        verificationCodeService.markCodeAsUsed(
                customer.getId(), verificationDto.getVerificationCode(), "LOGIN");

        // 4. DTO'ya dönüştür ve döndür
        DtoCustomerProfile profile = new DtoCustomerProfile();
        BeanUtils.copyProperties(customer, profile);
        return profile;
    }
}