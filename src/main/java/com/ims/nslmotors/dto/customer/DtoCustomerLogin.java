package com.ims.nslmotors.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoCustomerLogin {

    @NotBlank(message = "Email zorunludur.")
    @Email(message = "Geçerli bir email adresi giriniz.")
    private String email;

    @NotBlank(message = "Şifre zorunludur.")
    private String password;
}

