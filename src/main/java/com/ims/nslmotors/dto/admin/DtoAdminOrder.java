package com.ims.nslmotors.dto.admin;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class DtoAdminOrder {

    private Long id;
    private String orderNumber;
    private String stageSelected;
    private LocalDateTime orderDate;
    private LocalDateTime appointmentDate;
    private String status;

    // İlişkili Entity ID'leri
    private Long customerId;
    private Long technicianId;
    private Long carModelId;

    // Ek Bilgiler (Gösterim kolaylığı için Service'te doldurulur)
    private String customerFullName;
    private String carModelMakeAndModel;

    // Fatura ID'si (Opsiyonel)
    private Long invoiceId;
}