package com.ims.nslmotors.services;

public interface IMailService {

    /**
     * Doğrulama kodu içeren email gönderir
     * @param toEmail Alıcı email adresi
     * @param verificationCode Gönderilecek doğrulama kodu
     * @param purpose Kodun amacı (LOGIN, REGISTRATION, vb.)
     */
    void sendVerificationCode(String toEmail, String verificationCode, String purpose);
}

