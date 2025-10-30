package com.ims.nslmotors.dto.admin;

import lombok.Data;

@Data
public class DtoAdminCustomer {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
