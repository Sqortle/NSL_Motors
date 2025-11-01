package com.ims.nslmotors.dto.employee;

import lombok.Data;

@Data
public class DtoEmployeeProfile {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    // Teknisyen Detayları
    private String tcKimlikNo;
    private String address;
    private String shopName;
}