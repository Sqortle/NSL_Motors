package com.ims.nslmotors.dto.admin;

import lombok.Data;
import java.io.Serializable;

// Tüm alanlar? i?erir ve Long tipini kullan?r (null olabilmesi i?in)
@Data
public class DtoAdminCar implements Serializable {

    private Long id;
    private String make;
    private String model;
    private Integer year;
    private Integer stockHp;
    private Integer stage1Hp;
    private Integer stage2Hp;
    private Integer stage3Hp;
    private String carImageUrl;
}