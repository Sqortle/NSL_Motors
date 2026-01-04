package com.ims.nslmotors.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DtoPaymentRequest {
    
    @NotNull(message = "Randevu ID gereklidir")
    private Long appointmentId;
    
    @NotBlank(message = "Kart sahibi adı gereklidir")
    private String cardHolderName;
    
    @NotBlank(message = "Kart numarası gereklidir")
    @Pattern(regexp = "\\d{16}", message = "Kart numarası 16 haneli olmalıdır")
    private String cardNumber;
    
    @NotBlank(message = "Son kullanma tarihi gereklidir")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Son kullanma tarihi MM/YY formatında olmalıdır")
    private String expiryDate;
    
    @NotBlank(message = "CVV gereklidir")
    @Pattern(regexp = "\\d{3,4}", message = "CVV 3 veya 4 haneli olmalıdır")
    private String cvv;
    
    private String verificationCode; // Mail doğrulama kodu
}
