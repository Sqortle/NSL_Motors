// CustomerAuthServiceImpl.java
package com.ims.nslmotors.services.customer.impl;

import com.ims.nslmotors.dto.customer.DtoCustomerProfile;
import com.ims.nslmotors.dto.customer.DtoCustomerRegistirationIU;
import com.ims.nslmotors.services.customer.ICustomerAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerAuthServiceImpl implements ICustomerAuthService {

    // private final CustomerRepository customerRepository; // DI
    // private final MailService mailService; // DI

    // Implementasyonlar buraya gelecek (initiateRegistration, verifyAndActivateAccount, vb.)

    @Override
    public void initiateRegistration(DtoCustomerRegistirationIU registrationDto) {
        // 1. DTO'yu al, geçerliliğini kontrol et.
        // 2. Veritabanında email'in olup olmadığını kontrol et.
        // 3. Rastgele doğrulama kodu üret.
        // 4. Müşteriyi 'UNVERIFIED' durumuyla kaydet (Veya ayrı bir 'PendingUser' tablosuna).
        // 5. MailServis ile kodu gönder.
        System.out.println("Kayıt başlatıldı ve doğrulama kodu gönderildi.");
    }

    @Override
    public DtoCustomerProfile verifyAndActivateAccount(String email, String verificationCode) {
        // 1. Email ve kodu kontrol et.
        // 2. Doğruysa, müşterinin durumunu 'ACTIVE' olarak güncelle.
        // 3. Başarılı DTO'yu döndür.
        return new DtoCustomerProfile(); // Yer tutucu
    }
}