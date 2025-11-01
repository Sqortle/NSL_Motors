package com.ims.nslmotors.dto.employee;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DtoEmployeeOrder {

    private String orderNumber;
    private String customerFullName; // Müşterinin tam adı

    // Seçilen Araç Modeli Bilgileri
    private String carMake;
    private String carModel;

    private String stageSelected;
    private LocalDateTime orderDate;
    private String status;

    // Ek Bilgi: İlişkili Fatura Durumu
    private String invoiceStatus;
}