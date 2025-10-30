package com.ims.nslmotors.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal; // Fiyat için BigDecimal kullanıyoruz

@Entity
@Table(name = "car_models")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "make", length = 100, nullable = false)
    private String make;

    @Column(name = "model", length = 100, nullable = false)
    private String model;

    @Column(name = "year")
    private Integer year;

    @Column(name = "stock_hp")
    private Integer stockHp;

    @Column(name = "stage1_hp")
    private Integer stage1Hp;
    @Column(name = "stage2_hp")
    private Integer stage2Hp;
    @Column(name = "stage3_hp")
    private Integer stage3Hp;

    // YENİ KOLONLAR: Stage Fiyatları
    @Column(name = "stage1_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal stage1Price;

    @Column(name = "stage2_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal stage2Price;

    @Column(name = "stage3_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal stage3Price;

    @Column(name = "car_image_url")
    private String carImageUrl;
}