package com.ims.nslmotors.dto.customer;

import lombok.Data;

// Güvenli profil bilgisini dışarıya döndürmek için kullanılır.
@Data
public class DtoCustomerProfile {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    // NOT: Password ve verificationCode alanlar? yoktur.
}