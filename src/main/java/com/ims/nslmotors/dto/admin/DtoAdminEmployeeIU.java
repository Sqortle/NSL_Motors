package com.ims.nslmotors.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DtoAdminEmployeeIU {

    private Long id;


    @NotBlank(message = "Rol alanı zorunludur. (ADMIN, TECHNICIAN, MASTER)")
    @Pattern(regexp = "^(ADMIN|TECHNICIAN|MASTER)$", message = "Rol sadece ADMIN, TECHNICIAN veya MASTER olmalıdır.")
    private String role;

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
    private String password; // CREATE işlemi için zorunludur

    @Size(max = 20, message = "Telefon numarası 20 karakterden uzun olamaz.")
    private String phoneNumber;

    @Size(max = 100, message = "Dükkan adı 100 karakteri geçemez.")
    private String shopName;

    private String address; // Adres i?in ?zel bir k?s?tlay?c? yok

    @Size(min = 11, max = 11, message = "TC Kimlik No 11 haneli olmalıdır.")
    @Pattern(regexp = "^[0-9]*$", message = "TC Kimlik No sadece rakamlardan oluşmalıdır.")
    private String tcKimlikNo;
}

