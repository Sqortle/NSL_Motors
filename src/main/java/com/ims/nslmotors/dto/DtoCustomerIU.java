package com.ims.nslmotors.dto;

import jakarta.validation.constraints.*; // Validation k?t?phanesi import edildi
import lombok.Data;

// DTO: Customer Girdi/G?ncelleme (Input/Update)
@Data
public class DtoCustomerIU {

    // ID, sadece g?ncelleme (Update) senaryosunda kullan?labilir
    private Long id;

    @NotBlank(message = "Ad alanı zorunludur.")
    @Size(min = 2, max = 100, message = "Ad 2 ile 100 karakter arasında olmalıdır.")
    private String firstName;

    @NotBlank(message = "Soyad alanı zorunludur.")
    @Size(min = 2, max = 100, message = "Soyad 2 ile 100 karakter arasında olmalıdır.")
    private String lastName;

    @NotBlank(message = "Email alanı zorunludur.")
    @Email(message = "Geçerli bir email adresi giriniz.")
    private String email;

    @NotBlank(message = "Şifre alanı zorunludur.")
    @Size(min = 8, message = "Şifre minimum 8 karakter olmalıdır.")
    private String password; // CREATE işlemi için password zorunludur

    @Size(max = 20, message = "Telefon numarası 20 karakterden uzun olamaz.")
    private String phoneNumber;
}