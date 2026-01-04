package com.ims.nslmotors.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "shops")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "address", length = 500, nullable = false)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Dükkanın çalışma saatleri (JSON string olarak tutulabilir)
    @Column(name = "working_hours", length = 500)
    private String workingHours; // Örn: "09:00-18:00"

    // Bir dükkanda birden fazla çalışan olabilir
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees;

    // Bir dükkanda birden fazla randevu olabilir
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments;
}
