package com.ims.nslmotors.services;

public interface IMailService {

    /**
     * Doğrulama kodu içeren email gönderir
     * 
     * @param toEmail          Alıcı email adresi
     * @param verificationCode Gönderilecek doğrulama kodu
     * @param purpose          Kodun amacı (LOGIN, REGISTRATION, vb.)
     */
    void sendVerificationCode(String toEmail, String verificationCode, String purpose);

    /**
     * İletişim formundan gelen mesajı email olarak gönderir
     * 
     * @param fromEmail Gönderen email adresi
     * @param name      Gönderen adı
     * @param message   Mesaj içeriği
     */
    void sendContactMessage(String fromEmail, String name, String message);

    /**
     * Basit metin email gönderir
     * 
     * @param to      Alıcı email adresi
     * @param subject Email konusu
     * @param text    Email içeriği
     */
    void sendSimpleMessage(String to, String subject, String text);

    /**
     * HTML email gönderir
     * 
     * @param to          Alıcı email adresi
     * @param subject     Email konusu
     * @param htmlContent HTML içerik
     */
    void sendHtmlMessage(String to, String subject, String htmlContent);

    /**
     * PDF eki ile HTML email gönderir
     * 
     * @param to          Alıcı email adresi
     * @param subject     Email konusu
     * @param htmlContent HTML içerik
     * @param pdfContent  PDF dosya içeriği (byte array)
     * @param pdfFileName PDF dosya adı
     */
    void sendHtmlMessageWithPdfAttachment(String to, String subject, String htmlContent,
            byte[] pdfContent, String pdfFileName);
}
