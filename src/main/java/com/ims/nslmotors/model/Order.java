package com.ims.nslmotors.model;

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

    // İlişki 1: Müşteri (Many-to-One) - Customer Entity'sine ba?l?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // İlişki 2: Atanan Teknisyen/Çalışan (Many-to-One) - Employee Entity'sine ba?l?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private Employee technician;

    // İlişki 3: Araba Modeli (Many-to-One)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "model_id", nullable = false)
    private CarModel carModel;

    @Column(name = "stage_selected", length = 50, nullable = false)
    private String stageSelected;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "appointment_date")
    private LocalDateTime appointmentDate;

    @Column(name = "status", length = 50, nullable = false)
    private String status;

    // İlişki: One-to-One (Fatura)
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Invoice invoice;
}