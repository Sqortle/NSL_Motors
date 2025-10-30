package com.ims.nslmotors.dto.admin;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DtoAdminInvoice implements Serializable {

    // Fatura Alanlar?
    private Long id; // Order ID ile aynı
    private LocalDateTime invoiceDate;
    private BigDecimal subtotalAmount;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String status;

    // Arama ve G?sterim i?in ?li?kili Bilgiler
    private Long orderId; // Sipariş ID'si
    private String orderNumber; // KRİTİK: Sipariş Numarası Arama Kriteri
}