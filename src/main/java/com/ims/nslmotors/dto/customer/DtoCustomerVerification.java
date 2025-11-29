package com.ims.nslmotors.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DtoCustomerVerification {

    @NotBlank(message = "Email zorunludur.")
    @Email(message = "Geçerli bir email adresi giriniz.")
    private String email;

    @NotBlank(message = "Doğrulama kodu zorunludur.")
    @Size(min = 6, max = 6, message = "Doğrulama kodu 6 haneli olmalıdır.")
    private String verificationCode;
}

