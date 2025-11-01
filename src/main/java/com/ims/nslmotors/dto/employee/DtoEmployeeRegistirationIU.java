package com.ims.nslmotors.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class DtoEmployeeRegistirationIU {

    // ID, yaln?zca g?ncelleme (Update) i?lemlerinde kullan?l?r, kay?t (Insert) i?in bo? b?rak?l?r.
    private Long id;

    // Temel Ki?isel Bilgiler - Zorunlu Alanlar
    @NotBlank(message = "İsim alanı boş bırakılamaz.")
    @Size(max = 100, message = "İsim 100 karakterden uzun olamaz.")
    private String firstName;

    @NotBlank(message = "Soyisim alanı boş bırakılamaz.")
    @Size(max = 100, message = "Soyisim 100 karakterden uzun olamaz.")
    private String lastName;

    @NotBlank(message = "E-posta alanı zorunludur.")
    @Email(message = "Geçerli bir e-posta formatı giriniz.")
    @Size(max = 150, message = "E-posta 150 karakterden uzun olamaz.")
    private String email;

    @NotBlank(message = "Telefon numarası zorunludur.")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Geçersiz telefon numarası formatı.")
    private String phoneNumber;

    // Teknisyen Detaylar? (TechnicianDetails tablosundan)

    // TC Kimlik Numaras?: Zorunlu ve belirli uzunlukta olmal?
    @NotBlank(message = "TC Kimlik Numarası zorunludur.")
    @Size(min = 11, max = 11, message = "TC Kimlik Numarası 11 hane olmalıdır.")
    private String tcKimlikNo;

    @NotBlank(message = "Adres alanı boş bırakılamaz.")
    @Size(max = 255, message = "Adres 255 karakterden uzun olamaz.")
    private String address;

    @NotBlank(message = "Dükkan/Şirket Adı boş bırakılamaz.")
    @Size(max = 100, message = "Dükkan/Şirket Adı 100 karakterden uzun olamaz.")
    private String shopName;

    // ?ifre: Yaln?zca yeni kay?tta (Insert) zorunlu olmal?, g?ncellemede opsiyonel b?rak?labilir.
    // Bu mant?k i?in @NotBlank yerine, serviste ko?ullu kontrol yap?lmas? daha uygundur.
    @Size(min = 6, max = 100, message = "Şifre en az 6 karakter olmalıdır.")
    private String password;
}