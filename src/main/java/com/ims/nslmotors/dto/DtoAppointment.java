package com.ims.nslmotors.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class DtoAppointment implements Serializable {
    
    private Long id;
    private Long customerId;
    private String customerName;
    private Long carId;
    private String carName; // Make + Model
    private Long shopId;
    private String shopName;
    private Long technicianId;
    private String technicianName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Integer selectedStage;
    private BigDecimal price;
    private String status;
    private String paymentStatus;
    private String paymentMethod;
    private String transactionReference;
    private LocalDateTime createdAt;
    private String notes;
}
