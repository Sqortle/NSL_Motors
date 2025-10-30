package com.ims.nslmotors.dto.admin;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DtoAdminInvoiceIU {

    private Long id; // Güncelleme için gereklidir

    @NotNull(message = "Sipariş ID'si zorunludur.")
    private Long orderId; // Faturanın hangi siparişe ait olduğu

    @NotNull(message = "Fatura tarihi zorunludur.")
    private LocalDateTime invoiceDate;

    @NotNull(message = "Tutar zorunludur.")
    @DecimalMin(value = "0.00", message = "Tutar negatif olamaz.")
    private BigDecimal subtotalAmount;

    @NotNull(message = "Vergi oranı zorunludur.")
    private BigDecimal taxRate;

    // Bu alanlar Service'te hesaplanabilir ancak DTO'da tutulur
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    private LocalDateTime paymentDate;
    private String paymentMethod;

    @NotBlank(message = "Durum zorunludur.")
    private String status;
}