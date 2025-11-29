package com.ims.nslmotors.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DtoCustomerProfileUpdate {
    
    @NotBlank(message = "Ad zorunludur.")
    @Size(min = 2, max = 100, message = "Ad 2 ile 100 karakter arasında olmalıdır.")
    private String firstName;

    @NotBlank(message = "Soyad zorunludur.")
    @Size(min = 2, max = 100, message = "Soyad 2 ile 100 karakter arasında olmalıdır.")
    private String lastName;

    @NotBlank(message = "Email zorunludur.")
    @Email(message = "Geçerli bir email adresi giriniz.")
    private String email;

    @Size(max = 20, message = "Telefon numarası 20 karakterden uzun olamaz.")
    private String phoneNumber;
}

