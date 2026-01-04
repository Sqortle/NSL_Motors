package com.ims.nslmotors.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class DtoShop implements Serializable {
    
    private Long id;
    private String name;
    private String address;
    private String city;
    private String phoneNumber;
    private String email;
    private Boolean isActive;
    private String workingHours;
}
