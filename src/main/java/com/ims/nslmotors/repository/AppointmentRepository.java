package com.ims.nslmotors.repository;

import com.ims.nslmotors.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // Müşteriye göre randevular
    List<Appointment> findByCustomerId(Long customerId);
    
    // Müşteriye göre randevular (sıralı)
    List<Appointment> findByCustomerIdOrderByAppointmentDateDescAppointmentTimeDesc(Long customerId);
    
    // Belirli bir dükkan ve tarihteki randevuları getir
    List<Appointment> findByShopIdAndAppointmentDate(Long shopId, LocalDate date);
    
    // Belirli bir teknisyenin randevuları
    List<Appointment> findByTechnicianId(Long technicianId);
    
    // Belirli bir dükkan, tarih ve saatte randevu var mı kontrol et
    boolean existsByShopIdAndAppointmentDateAndAppointmentTime(Long shopId, LocalDate date, LocalTime time);
    
    // Belirli bir teknisyen, tarih ve saatte randevu var mı kontrol et
    boolean existsByTechnicianIdAndAppointmentDateAndAppointmentTime(Long technicianId, LocalDate date, LocalTime time);
    
    // Belirli bir dükkan ve tarih aralığındaki randevuları getir
    @Query("SELECT a FROM Appointment a WHERE a.shop.id = :shopId AND a.appointmentDate BETWEEN :startDate AND :endDate")
    List<Appointment> findAppointmentsByShopAndDateRange(
        @Param("shopId") Long shopId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // Duruma göre randevular
    List<Appointment> findByStatus(String status);
    
    // Ödeme durumuna göre randevular
    List<Appointment> findByPaymentStatus(String paymentStatus);
    
    // Transaction reference'a göre randevu bul
    Optional<Appointment> findByTransactionReference(String transactionReference);
}
