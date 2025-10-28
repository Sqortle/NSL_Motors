package com.ims.nslmotors.dto;

import com.ims.nslmotors.repository.CustomerRepository;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DtoCustomer {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
