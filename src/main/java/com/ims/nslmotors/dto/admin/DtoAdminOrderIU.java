package com.ims.nslmotors.dto.admin;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class DtoAdminOrderIU {

    // UPDATE işlemi için gereklidir
    private Long id;

    // --- Zorunlu İlişkili ID'ler ---
    @NotNull(message = "Müşteri ID'si zorunludur.")
    private Long customerId;

    // Teknisyen ID'si atanmayabilir (UPDATE i?in null olabilir)
    private Long technicianId;

    @NotNull(message = "Araba Modeli ID'si zorunludur.")
    private Long carModelId;

    // --- Sipariş Detayları ---
    @NotBlank(message = "Stage seçimi zorunludur.")
    @Pattern(regexp = "^(STAGE1|STAGE2)$", message = "Stage sadece STAGE1 veya STAGE2 olmalıdır.")
    private String stageSelected;

    // Randevu tarihi (UPDATE i?in ?nemli)
    private LocalDateTime appointmentDate;

    @NotBlank(message = "Sipariş durumu zorunludur.")
    private String status; // Örn: PENDING, COMPLETED
}