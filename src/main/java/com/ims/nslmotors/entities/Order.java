package com.ims.nslmotors.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", length = 8, unique = true, nullable = false)
    private String orderNumber;

    // İlişki 1: Müşteri (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // İlişki 2: Teknisyen/Vendor (Many-to-One) - Randevu için
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private User technician;

    // İlişki 3: Araba Modeli (Many-to-One)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "model_id", nullable = false)
    private CarModel carModel;

    @Column(name = "stage_selected", length = 50, nullable = false)
    private String stageSelected; // Örn: "STAGE1" veya "STAGE2"

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate; // Siparişin oluşturulma tarihi

    // Randevu Tarihini ayrı tutalım (Müşteri senaryosuna uygun)
    @Column(name = "appointment_date")
    private LocalDateTime appointmentDate;

    @Column(name = "status", length = 50, nullable = false)
    private String status; // Örn: "PENDING", "COMPLETED", "CANCELED"

    // İlişki: One-to-One (Bu siparişin faturası)
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Invoice invoice;
}