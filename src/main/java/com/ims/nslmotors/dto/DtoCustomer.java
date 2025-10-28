package com.ims.nslmotors.dto;

import com.ims.nslmotors.repository.CustomerRepository;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DtoCustomer {


    private long id;

    @NotBlank(message = "Ad alanı boş bırakılamaz.")
    @Size(min = 2, max = 100, message = "Ad 2 ile 100 karakter arasında olmalıdır.")
    private String firstName;

    @NotBlank(message = "Soyad alanı boş bırakılamaz.")
    @Size(min = 2, max = 100, message = "Soyad 2 ile 100 karakter arasında olmalıdır.")
    private String lastName;

    @NotBlank(message = "Email alanı boş bırakılamaz.")
    @Email(message = "Geçerli bir email adresi giriniz.")
    private String email;


    // Telefon numaras? zorunlu de?ilse:
    @Size(max = 20, message = "Telefon numaras? 20 karakterden uzun olamaz.")
    private String phoneNumber;

    // Not: Müşteri senaryosunda mail doğrulaması olacaksa, buraya
    // private String verificationCode; alan? da eklenebilir.

}
