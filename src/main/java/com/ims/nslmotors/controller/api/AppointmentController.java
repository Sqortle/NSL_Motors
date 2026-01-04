package com.ims.nslmotors.controller.api;

import com.ims.nslmotors.dto.DtoAppointment;
import com.ims.nslmotors.dto.DtoAppointmentRequest;
import com.ims.nslmotors.dto.DtoTechnician;
import com.ims.nslmotors.services.IAppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppointmentController {
    
    private final IAppointmentService appointmentService;
    
    // Randevu oluştur
    @PostMapping
    public ResponseEntity<DtoAppointment> createAppointment(@Valid @RequestBody DtoAppointmentRequest request) {
        try {
            DtoAppointment appointment = appointmentService.createAppointment(request);
            return new ResponseEntity<>(appointment, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // Müşterinin randevularını getir
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<DtoAppointment>> getCustomerAppointments(@PathVariable Long customerId) {
        List<DtoAppointment> appointments = appointmentService.getCustomerAppointments(customerId);
        return ResponseEntity.ok(appointments);
    }
    
    // Müsait saatleri getir
    @GetMapping("/available-times")
    public ResponseEntity<List<LocalTime>> getAvailableTimeSlots(
            @RequestParam Long shopId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<LocalTime> availableTimes = appointmentService.getAvailableTimeSlots(shopId, date);
        return ResponseEntity.ok(availableTimes);
    }
    
    // Dükkandaki teknisyenleri getir
    @GetMapping("/technicians/{shopId}")
    public ResponseEntity<List<DtoTechnician>> getTechniciansByShop(@PathVariable Long shopId) {
        List<DtoTechnician> technicians = appointmentService.getTechniciansByShop(shopId);
        return ResponseEntity.ok(technicians);
    }
    
    // Randevu detayını getir
    @GetMapping("/{id}")
    public ResponseEntity<DtoAppointment> getAppointmentById(@PathVariable Long id) {
        DtoAppointment appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }
    
    // Randevu durumunu güncelle
    @PatchMapping("/{id}/status")
    public ResponseEntity<DtoAppointment> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        DtoAppointment appointment = appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok(appointment);
    }
    
    // Randevu iptal et
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
