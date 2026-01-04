package com.ims.nslmotors.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DtoAppointmentRequest {
    
    @NotNull(message = "Müşteri ID gereklidir")
    private Long customerId;
    
    @NotNull(message = "Araba ID gereklidir")
    private Long carId;
    
    @NotNull(message = "Dükkan ID gereklidir")
    private Long shopId;
    
    @NotNull(message = "Teknisyen ID gereklidir")
    private Long technicianId;
    
    @NotNull(message = "Randevu tarihi gereklidir")
    private LocalDate appointmentDate;
    
    @NotNull(message = "Randevu saati gereklidir")
    private LocalTime appointmentTime;
    
    @NotNull(message = "Stage seçimi gereklidir")
    private Integer selectedStage;
    
    private BigDecimal price;
    
    private String notes;
}
