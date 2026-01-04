package com.ims.nslmotors.dto.admin;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

// Tüm alanlar? i?erir ve Long tipini kullan?r (null olabilmesi i?in)
@Data
public class DtoAdminCar {

    private Long id;
    private String make;
    private String model;
    private Integer year;
    private Integer stockHp;
    private Integer stage1Hp;
    private Integer stage2Hp;
    private Integer stage3Hp;
    private BigDecimal stage1Price;
    private BigDecimal stage2Price;
    private BigDecimal stage3Price;
    private String carImageUrl;
    private String makeImageUrl;
}