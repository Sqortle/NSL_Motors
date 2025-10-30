package com.ims.nslmotors.dto.admin;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

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

    @NotNull(message = "Stage 1 fiyatı zorunludur.")
    @DecimalMin(value = "0.00", message = "Fiyat negatif olamaz.")
    private BigDecimal stage1Price;

    @NotNull(message = "Stage 2 fiyatı zorunludur.")
    @DecimalMin(value = "0.00", message = "Fiyat negatif olamaz.")
    private BigDecimal stage2Price;

    @NotNull(message = "Stage 3 fiyatı zorunludur.")
    @DecimalMin(value = "0.00", message = "Fiyat negatif olamaz.")
    private BigDecimal stage3Price;

    private String carImageUrl;}