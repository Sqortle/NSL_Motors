package com.ims.nslmotors.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class DtoTechnician implements Serializable {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String role;
    private String email;
    private String phoneNumber;
    private Long shopId;
    private String shopName;
}
