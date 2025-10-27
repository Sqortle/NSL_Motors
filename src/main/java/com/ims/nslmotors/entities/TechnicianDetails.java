package com.ims.nslmotors.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "technician_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianDetails {

    // PRIMARY KEY ve FOREIGN KEY olarak Users tablosuna bağlanır
    @Id
    @Column(name = "user_id")
    private Long id;

    // İlişki: One-to-One (Users tablosuna bağlanır)
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // User'ın ID'sini PK ve FK olarak kullanır.
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "address")
    private String address;

    @Column(name = "shop_name", length = 100, nullable = false)
    private String shopName;

    @Column(name = "tc_kimlik_no", length = 11, unique = true)
    private String tcKimlikNo;
}