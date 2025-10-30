package com.ims.nslmotors.dto.admin;

import lombok.Data;

@Data
public class DtoAdminEmployee {

    private Long id;
    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String shopName;
    private String address;
    private String tcKimlikNo;

}
