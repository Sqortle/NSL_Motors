// ICustomerAuthService.java
package com.ims.nslmotors.services.customer;

import com.ims.nslmotors.dto.customer.DtoCustomerRegistirationIU;
import com.ims.nslmotors.dto.customer.DtoCustomerProfile;
import com.ims.nslmotors.dto.customer.DtoCustomerLogin;
import com.ims.nslmotors.dto.customer.DtoCustomerVerification;

public interface ICustomerAuthService {

    // Kayıt işlemini başlatır ve mail kodu gönderir
    void initiateRegistration(DtoCustomerRegistirationIU registrationDto);

    // Doğrulama kodunu kontrol eder ve hesabı etkinleştirir
    DtoCustomerProfile verifyAndActivateAccount(String email, String verificationCode);

    // Giriş işlemi: Email ve şifre kontrolü yapar, doğrulama kodu gönderir
    void initiateLogin(DtoCustomerLogin loginDto);

    // Giriş doğrulama kodunu kontrol eder
    DtoCustomerProfile verifyLogin(DtoCustomerVerification verificationDto);
}