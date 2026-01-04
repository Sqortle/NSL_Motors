package com.ims.nslmotors.services.impl;

import com.ims.nslmotors.dto.DtoAppointment;
import com.ims.nslmotors.dto.DtoAppointmentRequest;
import com.ims.nslmotors.dto.DtoTechnician;
import com.ims.nslmotors.model.*;
import com.ims.nslmotors.repository.*;
import com.ims.nslmotors.repository.admin.AdminCarRepository;
import com.ims.nslmotors.repository.admin.AdminOrderRepository;
import com.ims.nslmotors.services.IAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ShopRepository shopRepository;
    private final AdminCarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final AdminOrderRepository orderRepository;

    // Helper: Entity'den DTO'ya dönüşüm
    private DtoAppointment convertToDto(Appointment appointment) {
        DtoAppointment dto = new DtoAppointment();
        dto.setId(appointment.getId());
        dto.setCustomerId(appointment.getCustomer().getId());
        dto.setCustomerName(appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName());
        dto.setCarId(appointment.getCar().getId());
        dto.setCarName(appointment.getCar().getMake() + " " + appointment.getCar().getModel());
        dto.setShopId(appointment.getShop().getId());
        dto.setShopName(appointment.getShop().getName());
        dto.setTechnicianId(appointment.getTechnician().getId());
        dto.setTechnicianName(
                appointment.getTechnician().getFirstName() + " " + appointment.getTechnician().getLastName());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setSelectedStage(appointment.getSelectedStage());
        dto.setPrice(appointment.getPrice());
        dto.setStatus(appointment.getStatus());
        dto.setPaymentStatus(appointment.getPaymentStatus());
        dto.setPaymentMethod(appointment.getPaymentMethod());
        dto.setTransactionReference(appointment.getTransactionReference());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setNotes(appointment.getNotes());
        return dto;
    }

    // Helper: Teknisyen Entity'den DTO'ya dönüşüm
    private DtoTechnician convertTechnicianToDto(Employee employee) {
        DtoTechnician dto = new DtoTechnician();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setRole(employee.getRole());
        dto.setEmail(employee.getEmail());
        dto.setPhoneNumber(employee.getPhoneNumber());
        if (employee.getShop() != null) {
            dto.setShopId(employee.getShop().getId());
            dto.setShopName(employee.getShop().getName());
        }
        return dto;
    }

    @Override
    @Transactional
    public DtoAppointment createAppointment(DtoAppointmentRequest request) {
        // Validasyonlar
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Müşteri bulunamadı"));

        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new RuntimeException("Araba bulunamadı"));

        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new RuntimeException("Dükkan bulunamadı"));

        Employee technician = employeeRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> new RuntimeException("Teknisyen bulunamadı"));

        // Aynı tarih ve saatte başka randevu var mı kontrol et
        boolean exists = appointmentRepository.existsByShopIdAndAppointmentDateAndAppointmentTime(
                request.getShopId(), request.getAppointmentDate(), request.getAppointmentTime());

        if (exists) {
            throw new RuntimeException("Bu tarih ve saatte başka bir randevu bulunmaktadır");
        }

        // Teknisyenin aynı tarih ve saatte başka randevusu var mı kontrol et
        boolean technicianBusy = appointmentRepository.existsByTechnicianIdAndAppointmentDateAndAppointmentTime(
                request.getTechnicianId(), request.getAppointmentDate(), request.getAppointmentTime());

        if (technicianBusy) {
            throw new RuntimeException("Seçilen teknisyen bu tarih ve saatte müsait değil");
        }

        // Randevu oluştur
        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setCar(car);
        appointment.setShop(shop);
        appointment.setTechnician(technician);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setSelectedStage(request.getSelectedStage());
        appointment.setPrice(request.getPrice());
        appointment.setStatus("PENDING");
        appointment.setPaymentStatus("UNPAID");
        appointment.setNotes(request.getNotes());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Randevu oluşturulduğunda otomatik olarak sipariş oluştur
        createOrderFromAppointment(savedAppointment);

        return convertToDto(savedAppointment);
    }

    @Override
    public List<DtoAppointment> getCustomerAppointments(Long customerId) {
        return appointmentRepository.findByCustomerIdOrderByAppointmentDateDescAppointmentTimeDesc(customerId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocalTime> getAvailableTimeSlots(Long shopId, LocalDate date) {
        // Çalışma saatleri: 09:00 - 18:00 arası her saat
        List<LocalTime> allTimeSlots = new ArrayList<>();
        for (int hour = 9; hour <= 17; hour++) {
            allTimeSlots.add(LocalTime.of(hour, 0));
        }

        // O gün ve dükkanda mevcut randevuları al
        List<Appointment> existingAppointments = appointmentRepository.findByShopIdAndAppointmentDate(shopId, date);

        // Dolu saatleri çıkar
        List<LocalTime> bookedTimes = existingAppointments.stream()
                .map(Appointment::getAppointmentTime)
                .collect(Collectors.toList());

        // Müsait saatleri döndür
        return allTimeSlots.stream()
                .filter(time -> !bookedTimes.contains(time))
                .collect(Collectors.toList());
    }

    @Override
    public List<DtoTechnician> getTechniciansByShop(Long shopId) {
        // Belirli bir dükkana bağlı tüm çalışanları getir
        return employeeRepository.findByShopId(shopId)
                .stream()
                .map(this::convertTechnicianToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DtoAppointment getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı: ID = " + id));
        return convertToDto(appointment);
    }

    @Override
    @Transactional
    public DtoAppointment updateAppointmentStatus(Long id, String status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı: ID = " + id));

        appointment.setStatus(status);
        Appointment updated = appointmentRepository.save(appointment);

        return convertToDto(updated);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı: ID = " + id));

        appointment.setStatus("CANCELLED");
        appointmentRepository.save(appointment);
    }

    /**
     * Randevudan sipariş oluşturur (Invoice oluşturulmaz, henüz ödeme yapılmadığı
     * için)
     */
    private Order createOrderFromAppointment(Appointment appointment) {
        Order order = new Order();

        // Benzersiz sipariş numarası oluştur
        String orderNumber;
        do {
            long randomLong = (long) (Math.random() * (999999999999L - 100000000000L) + 100000000000L);
            orderNumber = String.valueOf(randomLong);
        } while (orderRepository.findByOrderNumber(orderNumber).isPresent());

        order.setOrderNumber(orderNumber);
        order.setCustomer(appointment.getCustomer());
        order.setCarModel(appointment.getCar());
        order.setTechnician(appointment.getTechnician());
        order.setStageSelected("STAGE" + appointment.getSelectedStage());
        order.setOrderDate(appointment.getCreatedAt() != null ? appointment.getCreatedAt() : LocalDateTime.now());
        order.setAppointmentDate(appointment.getAppointmentDate().atTime(appointment.getAppointmentTime()));
        order.setStatus("BEKLEMEDE"); // Randevu henüz onaylanmadığı için beklemede
        order.setAppointment(appointment);

        // Order'ı kaydet
        Order savedOrder = orderRepository.save(order);

        // Randevuya order'ı bağla
        appointment.setOrder(savedOrder);
        appointmentRepository.save(appointment);

        return savedOrder;
    }
}
