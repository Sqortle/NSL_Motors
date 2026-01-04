// DtoCustomerRegistiration.java
package com.ims.nslmotors.dto.customer;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.io.Serializable;

@Data
public class DtoCustomerRegistirationIU {

    @NotBlank(message = "Ad zorunludur.")
    @Size(min = 2, max = 100, message = "Ad 2 ile 100 karakter olmalıdır.")
    private String firstName;

    @NotBlank(message = "Soyad zorunludur.")
    private String lastName;

    @NotBlank(message = "Email zorunludur.")
    @Email(message = "Geçerli bir email adresi giriniz.")
    private String email;

    @NotBlank(message = "Şifre zorunludur.")
    @Size(min = 8, message = "Şifre minimum 8 karakter olmalıdır.")
    private String password;

    @NotBlank(message = "Şifre tekrarı zorunludur.")
    private String confirmPassword;

    // Doğrulama Kodu: Üyelik akışında zorunlu olacak
    @Size(min = 6, max = 6, message = "Doğrulama kodu 6 haneli olmalıdır.")
    private String verificationCode;

    private String phoneNumber;
}