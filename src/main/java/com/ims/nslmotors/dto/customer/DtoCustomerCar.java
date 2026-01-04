// DtoCustomerCar.java
package com.ims.nslmotors.dto.customer;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DtoCustomerCar {

    private Long id;
    private String make;
    private String model;
    private Integer year;
    private Integer stockHp;
    private Integer stage1Hp;
    private Integer stage2Hp;
    private Integer stage3Hp;
    private String carImageUrl;
    private String makeImageUrl;

    // Müşterinin göreceği fiyatlar
    private BigDecimal stage1Price;
    private BigDecimal stage2Price;
    private BigDecimal stage3Price;
}