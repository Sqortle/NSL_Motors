package com.ims.nslmotors.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "car_models")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "make", length = 100, nullable = false)
    private String make; // Marka

    @Column(name = "model", length = 100, nullable = false)
    private String model; // Model

    @Column(name = "year")
    private Integer year;

    @Column(name = "stock_hp")
    private Integer stockHp;

    @Column(name = "stage1_hp")
    private Integer stage1Hp;

    @Column(name = "stage2_hp")
    private Integer stage2Hp;

    @Column(name = "stage3_hp")
    private Integer stage3Hp; // Eklenen Stage 3 için

    @Column(name = "car_image_url")
    private String carImageUrl;

    // Not: Bu entity'den Orders'a Many-to-One ilişkisi olacak,
    // ancak burada ters ilişkiyi (mappedBy) tanımlamaya gerek yok.
}