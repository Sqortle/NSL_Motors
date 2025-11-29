package com.ims.nslmotors.services;

import com.ims.nslmotors.model.Customer;

public interface IVerificationCodeService {

    /**
     * Yeni bir doğrulama kodu oluşturur ve veritabanına kaydeder
     * @param customer Doğrulama kodu oluşturulacak müşteri
     * @param purpose Kodun amacı (LOGIN, REGISTRATION, ORDER_COMPLETION, vb.)
     * @return Oluşturulan 6 haneli doğrulama kodu
     */
    String generateAndSaveVerificationCode(Customer customer, String purpose);

    /**
     * Doğrulama kodunu kontrol eder
     * @param customerId Müşteri ID'si
     * @param code Doğrulama kodu
     * @param purpose Kodun amacı
     * @return Kod geçerli ve kullanılmamışsa true, aksi halde false
     */
    boolean verifyCode(Long customerId, String code, String purpose);

    /**
     * Doğrulama kodunu kullanılmış olarak işaretler
     * @param customerId Müşteri ID'si
     * @param code Doğrulama kodu
     * @param purpose Kodun amacı
     */
    void markCodeAsUsed(Long customerId, String code, String purpose);
}

