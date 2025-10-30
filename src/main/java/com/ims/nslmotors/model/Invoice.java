package com.ims.nslmotors.model;

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
    // PK ve FK olarak Order ID'sini kullan?r
    @Id
    @Column(name = "order_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Order'?n ID'sini PK ve FK olarak kullan?r.
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "invoice_date", nullable = false)
    private LocalDateTime invoiceDate;

    // Subtotal Amount (Hesaplanacak)
    @Column(name = "subtotal_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotalAmount;

    // Tax Rate (%20 statik olacak ama DB'de tutulur)
    @Column(name = "tax_rate", precision = 4, scale = 2, nullable = false)
    private BigDecimal taxRate;

    @Column(name = "tax_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal taxAmount;

    // Total Amount
    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate; // PaymentDate null olabilir (ödeme sonra yap?l?rsa)

    @Column(name = "payment_method", length = 50, nullable = false) // ?lk de?eri CreditCard olaca?? i?in nullable=false kals?n
    private String paymentMethod;

    @Column(name = "status", length = 50, nullable = false) // ?lk de?eri PENDING olaca?? i?in nullable=false kals?n
    private String status;
}