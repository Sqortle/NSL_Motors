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

    // PRIMARY KEY ve FOREIGN KEY olarak Orders tablosuna bağlanır
    @Id
    @Column(name = "order_id")
    private Long id;

    // İlişki: One-to-One (Orders tablosuna bağlanır)
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Order'ın ID'sini PK ve FK olarak kullanır.
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "invoice_date", nullable = false)
    private LocalDateTime invoiceDate;

    // DECIMAL için Java'da BigDecimal kullanmak en doğru yaklaşımdır.
    @Column(name = "subtotal_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotalAmount;

    @Column(name = "tax_rate", precision = 4, scale = 2, nullable = false)
    private BigDecimal taxRate; // Örn: 0.20 (Yani %20)

    @Column(name = "tax_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // Örn: "CREDIT_CARD", "TRANSFER"

    @Column(name = "status", length = 50, nullable = false)
    private String status; // Örn: "PAID", "PENDING", "FAILED"
}