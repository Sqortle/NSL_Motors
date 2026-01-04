package com.ims.nslmotors.services;

import com.ims.nslmotors.dto.DtoPaymentRequest;

public interface IPaymentService {
    
    // Kredi kartı numarasını Luhn algoritması ile doğrula
    boolean validateCreditCard(String cardNumber);
    
    // Mail doğrulama kodu gönder
    void sendPaymentVerificationCode(String email, Long appointmentId);
    
    // Mail doğrulama kodunu kontrol et
    boolean verifyPaymentCode(Long appointmentId, String code);
    
    // Ödemeyi işle
    String processPayment(DtoPaymentRequest request);
    
    // Fatura gönder
    void sendInvoice(Long appointmentId);
}
