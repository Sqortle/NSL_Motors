package com.ims.nslmotors.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DtoCustomerPasswordChange {
    
    @NotBlank(message = "Mevcut şifre zorunludur.")
    private String currentPassword;

    @NotBlank(message = "Yeni şifre zorunludur.")
    @Size(min = 8, message = "Yeni şifre minimum 8 karakter olmalıdır.")
    private String newPassword;

    @NotBlank(message = "Şifre tekrarı zorunludur.")
    private String confirmPassword;
}

