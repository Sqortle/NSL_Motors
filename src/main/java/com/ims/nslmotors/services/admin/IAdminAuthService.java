package com.ims.nslmotors.services.admin;

import com.ims.nslmotors.dto.admin.DtoAdminLogin;

public interface IAdminAuthService {
    
    /**
     * Admin giriş başlatır - email ve şifre kontrolü yapar, doğrulama kodu gönderir
     * @param loginDto Giriş bilgileri
     */
    void initiateLogin(DtoAdminLogin loginDto);
    
    /**
     * Doğrulama kodunu kontrol eder ve girişi tamamlar
     * @param email Admin email adresi
     * @param verificationCode Doğrulama kodu
     */
    void verifyLogin(String email, String verificationCode);
}

