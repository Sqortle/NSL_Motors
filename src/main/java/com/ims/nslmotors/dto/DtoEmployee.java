package com.ims.nslmotors.dto;

import com.ims.nslmotors.model.Order;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class DtoEmployee {

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
