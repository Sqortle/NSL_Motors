package com.ims.nslmotors.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Müşteri bilgisi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Araba bilgisi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    // Dükkan bilgisi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    // Teknisyen bilgisi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id", nullable = false)
    private Employee technician;

    // Randevu tarihi ve saati
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    // Seçilen stage (1, 2 veya 3)
    @Column(name = "selected_stage", nullable = false)
    private Integer selectedStage;

    // Fiyat
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    // Randevu durumu
    @Column(name = "status", length = 50, nullable = false)
    private String status = "PENDING"; // PENDING, CONFIRMED, COMPLETED, CANCELLED

    // Ödeme durumu
    @Column(name = "payment_status", length = 50, nullable = false)
    private String paymentStatus = "UNPAID"; // UNPAID, PAID, REFUNDED

    // Ödeme yöntemi
    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // CREDIT_CARD, CASH, etc.

    // İşlem referans numarası (ödeme için)
    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    // Oluşturulma tarihi
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Güncellenme tarihi
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Not/Açıklama
    @Column(name = "notes", length = 1000)
    private String notes;

    // Randevu ile ilişkili sipariş (opsiyonel)
    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Order order;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
