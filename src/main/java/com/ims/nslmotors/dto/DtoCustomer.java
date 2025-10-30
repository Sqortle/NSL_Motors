package com.ims.nslmotors.dto;

import lombok.Data;

@Data
public class DtoCustomer {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
