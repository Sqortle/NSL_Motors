package com.ims.nslmotors.services;

import com.ims.nslmotors.model.Customer;
import com.ims.nslmotors.model.Employee;

public interface IVerificationCodeService {

    /**
     * Yeni bir doğrulama kodu oluşturur ve veritabanına kaydeder
     * @param customer Doğrulama kodu oluşturulacak müşteri
     * @param purpose Kodun amacı (LOGIN, REGISTRATION, ORDER_COMPLETION, vb.)
     * @return Oluşturulan 6 haneli doğrulama kodu
     */
    String generateAndSaveVerificationCode(Customer customer, String purpose);

    /**
     * Employee için yeni bir doğrulama kodu oluşturur ve veritabanına kaydeder
     * @param employee Doğrulama kodu oluşturulacak çalışan
     * @param purpose Kodun amacı (LOGIN, REGISTRATION, vb.)
     * @return Oluşturulan 6 haneli doğrulama kodu
     */
    String generateAndSaveVerificationCodeForEmployee(Employee employee, String purpose);

    /**
     * Doğrulama kodunu kontrol eder
     * @param customerId Müşteri ID'si
     * @param code Doğrulama kodu
     * @param purpose Kodun amacı
     * @return Kod geçerli ve kullanılmamışsa true, aksi halde false
     */
    boolean verifyCode(Long customerId, String code, String purpose);

    /**
     * Employee için doğrulama kodunu kontrol eder
     * @param employeeId Çalışan ID'si
     * @param code Doğrulama kodu
     * @param purpose Kodun amacı
     * @return Kod geçerli ve kullanılmamışsa true, aksi halde false
     */
    boolean verifyCodeForEmployee(Long employeeId, String code, String purpose);

    /**
     * Email ile doğrulama kodunu kontrol eder (hem customer hem employee için)
     * @param email Email adresi
     * @param code Doğrulama kodu
     * @param purpose Kodun amacı
     * @return Kod geçerli ve kullanılmamışsa true, aksi halde false
     */
    boolean verifyCodeByEmail(String email, String code, String purpose);

    /**
     * Doğrulama kodunu kullanılmış olarak işaretler
     * @param customerId Müşteri ID'si
     * @param code Doğrulama kodu
     * @param purpose Kodun amacı
     */
    void markCodeAsUsed(Long customerId, String code, String purpose);

    /**
     * Employee için doğrulama kodunu kullanılmış olarak işaretler
     * @param employeeId Çalışan ID'si
     * @param code Doğrulama kodu
     * @param purpose Kodun amacı
     */
    void markCodeAsUsedForEmployee(Long employeeId, String code, String purpose);

    /**
     * Email ile doğrulama kodunu kullanılmış olarak işaretler (hem customer hem employee için)
     * @param email Email adresi
     * @param code Doğrulama kodu
     * @param purpose Kodun amacı
     */
    void markCodeAsUsedByEmail(String email, String code, String purpose);
}

