package com.ims.nslmotors.services;

import com.ims.nslmotors.dto.DtoAppointment;
import com.ims.nslmotors.dto.DtoAppointmentRequest;
import com.ims.nslmotors.dto.DtoTechnician;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IAppointmentService {
    
    // Randevu oluştur
    DtoAppointment createAppointment(DtoAppointmentRequest request);
    
    // Müşterinin randevularını getir
    List<DtoAppointment> getCustomerAppointments(Long customerId);
    
    // Belirli bir dükkan ve tarihteki müsait saatleri getir
    List<LocalTime> getAvailableTimeSlots(Long shopId, LocalDate date);
    
    // Belirli bir dükkandaki teknisyenleri getir
    List<DtoTechnician> getTechniciansByShop(Long shopId);
    
    // Randevu detayını getir
    DtoAppointment getAppointmentById(Long id);
    
    // Randevu durumunu güncelle
    DtoAppointment updateAppointmentStatus(Long id, String status);
    
    // Randevu iptal et
    void cancelAppointment(Long id);
}
