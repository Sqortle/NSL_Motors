package com.ims.nslmotors.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoAdminLogin {
    
    @NotBlank(message = "Email alanı zorunludur.")
    @Email(message = "Geçerli bir email adresi giriniz.")
    private String email;
    
    @NotBlank(message = "Şifre alanı zorunludur.")
    private String password;
}

