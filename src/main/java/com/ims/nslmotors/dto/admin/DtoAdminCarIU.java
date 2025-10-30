package com.ims.nslmotors.dto.admin;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.io.Serializable;

@Data
public class DtoAdminCarIU implements Serializable {

    private Long id;

    @NotBlank(message = "Marka zorunludur.")
    private String make;

    @NotBlank(message = "Model zorunludur.")
    private String model;

    @NotNull(message = "Yıl zorunludur.")
    @Min(value = 1900, message = "Ge?erli bir y?l giriniz.")
    private Integer year;

    @NotNull(message = "Stok HP zorunludur.")
    @Min(value = 50, message = "HP de?eri ?ok d???k.")
    private Integer stockHp;

    private Integer stage1Hp;
    private Integer stage2Hp;
    private Integer stage3Hp;

    private String carImageUrl;
}