package com.ims.nslmotors.controller.web;

import com.ims.nslmotors.dto.DtoAppointment;
import com.ims.nslmotors.dto.DtoPaymentRequest;
import com.ims.nslmotors.model.Appointment;
import com.ims.nslmotors.model.Customer;
import com.ims.nslmotors.repository.AppointmentRepository;
import com.ims.nslmotors.services.IAppointmentService;
import com.ims.nslmotors.services.IPaymentService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    
    private final IAppointmentService appointmentService;
    private final IPaymentService paymentService;
    private final AppointmentRepository appointmentRepository;
    
    @GetMapping("/payment")
    public String paymentPage(@RequestParam Long appointmentId, Model model, HttpSession session) {
        try {
            // Session kontrolü (şimdilik geçici olarak kontrol etmiyoruz)
            // Customer customer = (Customer) session.getAttribute("customer");
            // if (customer == null) {
            //     return "redirect:/customer/login?redirect=/payment?appointmentId=" + appointmentId;
            // }
            
            DtoAppointment appointment = appointmentService.getAppointmentById(appointmentId);
            
            model.addAttribute("appointment", appointment);
            model.addAttribute("title", "Ödeme - NSL Motors");
            
            return "payment";
        } catch (Exception e) {
            log.error("Error loading payment page: {}", e.getMessage());
            model.addAttribute("error", "Randevu bilgileri yüklenemedi");
            return "error";
        }
    }
    
    @PostMapping("/api/payment/send-verification")
    @ResponseBody
    public ResponseEntity<ApiResponse> sendVerificationCode(@RequestParam Long appointmentId) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Randevu bulunamadı"));
            
            String customerEmail = appointment.getCustomer().getEmail();
            paymentService.sendPaymentVerificationCode(customerEmail, appointmentId);
            
            return ResponseEntity.ok(new ApiResponse(true, "Doğrulama kodu email adresinize gönderildi"));
        } catch (Exception e) {
            log.error("Error sending verification code: {}", e.getMessage());
            return ResponseEntity.ok(new ApiResponse(false, "Doğrulama kodu gönderilemedi: " + e.getMessage()));
        }
    }
    
    @PostMapping("/api/payment/process")
    @ResponseBody
    public ResponseEntity<Object> processPayment(@RequestBody DtoPaymentRequest request) {
        try {
            // Kredi kartı validasyonu
            if (!paymentService.validateCreditCard(request.getCardNumber())) {
                return ResponseEntity.ok(new ApiResponse(false, "Geçersiz kredi kartı numarası"));
            }
            
            // Doğrulama kodu kontrolü
            if (request.getVerificationCode() == null || request.getVerificationCode().isEmpty()) {
                return ResponseEntity.ok(new ApiResponse(false, "Lütfen doğrulama kodunu girin"));
            }
            
            if (!paymentService.verifyPaymentCode(request.getAppointmentId(), request.getVerificationCode())) {
                return ResponseEntity.ok(new ApiResponse(false, "Geçersiz veya süresi dolmuş doğrulama kodu"));
            }
            
            // Ödemeyi işle
            String transactionRef = paymentService.processPayment(request);
            
            // Fatura gönder
            paymentService.sendInvoice(request.getAppointmentId());
            
            return ResponseEntity.ok(new PaymentResponse(true, "Ödeme başarılı!", transactionRef));
        } catch (Exception e) {
            log.error("Payment processing error: {}", e.getMessage());
            return ResponseEntity.ok(new ApiResponse(false, "Ödeme işlemi başarısız: " + e.getMessage()));
        }
    }
    
    @GetMapping("/payment/success")
    public String paymentSuccess(@RequestParam String transactionRef, Model model, HttpSession session) {
        // Transaction reference'dan appointment'ı bul ve customer bilgisini session'a ekle
        try {
            Appointment appointment = appointmentRepository.findByTransactionReference(transactionRef)
                    .orElse(null);
            
            if (appointment != null && appointment.getCustomer() != null) {
                Customer customer = appointment.getCustomer();
                // Session'a customer bilgilerini ekle (eğer yoksa)
                if (session.getAttribute("customerId") == null) {
                    session.setAttribute("customerId", customer.getId());
                    session.setAttribute("customerName", customer.getFirstName() + " " + customer.getLastName());
                    session.setAttribute("customerEmail", customer.getEmail());
                }
            }
        } catch (Exception e) {
            log.warn("Could not set customer session from transaction: {}", e.getMessage());
        }
        
        model.addAttribute("transactionRef", transactionRef);
        model.addAttribute("title", "Ödeme Başarılı - NSL Motors");
        return "payment-success";
    }
    
    // Inner classes for responses
    @Data
    @AllArgsConstructor
    static class ApiResponse {
        private boolean success;
        private String message;
    }
    
    @Data
    @lombok.EqualsAndHashCode(callSuper=true)
    static class PaymentResponse extends ApiResponse {
        private String transactionReference;
        
        public PaymentResponse(boolean success, String message, String transactionReference) {
            super(success, message);
            this.transactionReference = transactionReference;
        }
    }
}
