package com.ims.nslmotors.entities; // Veya com.ims.nslmotors.entity

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // SQL'deki INT yerine Java'da Long kullanmak best practice'dir

    @Column(name = "name", length = 50, unique = true, nullable = false)
    private String name; // Örn: ADMIN, VENDOR, CUSTOMER

    // Not: Role'den User'a olan ilişkiyi burada tanımlamayacağız (Unidirectional ManyToOne yeterli)
}