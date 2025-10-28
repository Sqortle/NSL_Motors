package com.ims.nslmotors.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    // PRIMARY KEY ve FOREIGN KEY olarak Orders tablosuna ba?lan?r
    @Id
    @Column(name = "order_id")
    private Long id;

    // İlişki: One-to-One (Orders tablosuna ba?lan?r)
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Order'?n ID'sini PK ve FK olarak kullan?r.
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "invoice_date", nullable = false)
    private LocalDateTime invoiceDate;

    @Column(name = "subtotal_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotalAmount;

    @Column(name = "tax_rate", precision = 4, scale = 2, nullable = false)
    private BigDecimal taxRate;

    @Column(name = "tax_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "status", length = 50, nullable = false)
    private String status;
}