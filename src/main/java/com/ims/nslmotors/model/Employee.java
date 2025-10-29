package com.ims.nslmotors.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Rol, String olarak tutulur: "ADMIN", "TECHNICIAN", "MASTER"
    @Column(name = "role", length = 50, nullable = false)
    private String role;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "email", length = 150, unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    // Çalışan Detayları (TechnicianDetails'tan taşındı)
    @Column(name = "shop_name", length = 100)
    private String shopName;

    @Column(name = "address")
    private String address;

    @Column(name = "tc_kimlik_no", length = 11, unique = true)
    private String tcKimlikNo;

    // KRİTİK DÜZELTME: mappedBy de?eri, Order.java'daki alan ad?na ("technician") ayarland?.
    @OneToMany(mappedBy = "technician", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> assignedOrders;
}