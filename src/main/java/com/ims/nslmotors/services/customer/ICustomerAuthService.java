// ICustomerAuthService.java
package com.ims.nslmotors.services.customer;

import com.ims.nslmotors.dto.customer.DtoCustomerRegistirationIU;
import com.ims.nslmotors.dto.customer.DtoCustomerProfile; // Yeni Profil DTO'su

public interface ICustomerAuthService {

    // Kayıt işlemini başlatır ve mail kodu gönderir
    void initiateRegistration(DtoCustomerRegistirationIU registrationDto);

    // Doğrulama kodunu kontrol eder ve hesabı etkinleştirir
    DtoCustomerProfile verifyAndActivateAccount(String email, String verificationCode);

    // Giriş işlemleri (ileride Security ile entegre edilecek)
    // DtoCustomerProfile login(String email, String password);
}