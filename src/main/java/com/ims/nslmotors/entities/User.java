package com.ims.nslmotors.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // İlişki: Many-to-One (Çok Kullanıcı bir Role sahip olabilir)
    // role_id sütununu oluşturur
    @ManyToOne(fetch = FetchType.EAGER) // EAGER: Rol bilgisini hemen yükle
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "email", length = 150, unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    // Güvenlik için şifre alanı eklenmiştir (Hayati Önem Taşır)
    @Column(name = "password", nullable = false)
    private String password;

    // İlişki: One-to-One (Teknisyen ise detayları buraya bağlanır)
    // MappedBy ile User'ın ana tablo olduğunu belirtiriz
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private TechnicianDetails technicianDetails;

    // İlişki: One-to-Many (Bir Müşterinin Birden Fazla Siparişi Olabilir)
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> customerOrders;

    // İlişki: One-to-Many (Bir Teknisyen Birden Fazla Siparişte Görev Alabilir)
    @OneToMany(mappedBy = "technician", cascade = CascadeType.ALL)
    private List<Order> technicianOrders;
}