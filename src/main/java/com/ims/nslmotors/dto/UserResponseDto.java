package com.ims.nslmotors.dto;

import lombok.Data;
import java.io.Serializable;

// Dışarıya gönderilecek kullanıcı verilerini taşır (Şifre hariç)
@Data
public class UserResponseDto implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    // Rol bilgisini String olarak taşıyacağız.
    private String roleName;

    // Not: Tüm alanlar, User Entity'sindeki alan adlarıyla aynı olmalıdır
    // ki BeanUtils.copyProperties() sorunsuz çalışsın.
}