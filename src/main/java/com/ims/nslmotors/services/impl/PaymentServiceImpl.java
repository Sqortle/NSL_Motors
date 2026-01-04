package com.ims.nslmotors.services.impl;

import com.ims.nslmotors.dto.DtoPaymentRequest;
import com.ims.nslmotors.model.*;
import com.ims.nslmotors.repository.AppointmentRepository;
import com.ims.nslmotors.repository.VerificationCodeRepository;
import com.ims.nslmotors.repository.admin.AdminOrderRepository;
import com.ims.nslmotors.repository.admin.AdminInvoiceRepository;
import com.ims.nslmotors.services.IMailService;
import com.ims.nslmotors.services.IPdfService;
import com.ims.nslmotors.services.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements IPaymentService {

    private final AppointmentRepository appointmentRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AdminOrderRepository orderRepository;
    private final AdminInvoiceRepository invoiceRepository;
    private final IMailService mailService;
    private final IPdfService pdfService;

    @Override
    public boolean validateCreditCard(String cardNumber) {
        // Luhn Algoritması (Mod 10)
        if (cardNumber == null || !cardNumber.matches("\\d+")) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;

        // Sağdan sola doğru
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = digit - 9;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        boolean isValid = (sum % 10 == 0);
        log.info("Credit card validation: {} - Result: {}",
                cardNumber.substring(0, 4) + "****" + cardNumber.substring(cardNumber.length() - 4),
                isValid);

        return isValid;
    }

    @Override
    @Transactional
    public void sendPaymentVerificationCode(String email, Long appointmentId) {
        // 6 haneli kod oluştur
        String code = String.format("%06d", new Random().nextInt(999999));

        // Appointment'ı bul
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı"));

        // Veritabanına kaydet
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setPurpose("PAYMENT");
        verificationCode.setCreatedAt(LocalDateTime.now());
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(10)); // 10 dakika geçerli
        verificationCode.setIsUsed(false);
        verificationCode.setAppointment(appointment);
        verificationCode.setCustomer(appointment.getCustomer());

        verificationCodeRepository.save(verificationCode);

        // Mail gönder
        String subject = "NSL Motors - Ödeme Doğrulama Kodu";
        String message = String.format(
                "Merhaba,\n\n" +
                        "Ödeme işleminizi tamamlamak için doğrulama kodunuz: %s\n\n" +
                        "Bu kod 10 dakika içinde geçerlidir.\n\n" +
                        "NSL Motors",
                code);

        mailService.sendSimpleMessage(email, subject, message);
        log.info("Payment verification code sent to: {}", email);
    }

    @Override
    public boolean verifyPaymentCode(Long appointmentId, String code) {
        LocalDateTime now = LocalDateTime.now();
        Optional<VerificationCode> verificationCodeOpt = verificationCodeRepository
                .findByCodeAndAppointmentIdAndIsUsedFalseAndExpiresAtAfter(code, appointmentId, now);
        return verificationCodeOpt.isPresent();
    }

    @Override
    @Transactional
    public String processPayment(DtoPaymentRequest request) {
        // Randevuyu bul
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı"));

        // Kredi kartını doğrula
        if (!validateCreditCard(request.getCardNumber())) {
            throw new RuntimeException("Geçersiz kredi kartı numarası");
        }

        // Mail doğrulama kodunu kontrol et
        if (request.getVerificationCode() != null) {
            if (!verifyPaymentCode(request.getAppointmentId(), request.getVerificationCode())) {
                throw new RuntimeException("Geçersiz doğrulama kodu");
            }
        }

        // Transaction reference oluştur
        String transactionRef = "NSL" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Randevuyu güncelle
        appointment.setPaymentStatus("PAID");
        appointment.setPaymentMethod("CREDIT_CARD");
        appointment.setTransactionReference(transactionRef);
        appointment.setStatus("CONFIRMED");

        appointmentRepository.save(appointment);

        // Order ve Invoice kontrolü
        Order order = appointment.getOrder();

        if (order == null) {
            // Order yoksa, hem Order hem Invoice oluştur
            order = createOrderFromAppointment(appointment, transactionRef);
            appointment.setOrder(order);
            appointmentRepository.save(appointment);
            log.info("Order and Invoice created from appointment: {}", order.getOrderNumber());
        } else {
            // Order varsa, Invoice kontrolü yap
            if (order.getInvoice() == null) {
                // Invoice yoksa oluştur
                Invoice invoice = createInvoiceForOrder(order, appointment);

                // @MapsId kullanıldığı için Order'a set edip Order'ı kaydediyoruz
                // Cascade ile Invoice otomatik kaydedilecek
                order.setInvoice(invoice);
                order.setStatus("ONAYLANDI"); // Ödeme yapıldığı için durumu güncelle
                orderRepository.save(order);

                log.info("Invoice created for existing order: {}", order.getOrderNumber());
            } else {
                // Invoice varsa sadece durumunu güncelle
                Invoice invoice = order.getInvoice();
                invoice.setStatus("PAID");
                invoice.setPaymentDate(LocalDateTime.now());
                invoice.setPaymentMethod("CREDIT_CARD");
                invoiceRepository.save(invoice);

                order.setStatus("ONAYLANDI");
                orderRepository.save(order);

                log.info("Invoice updated for order: {}", order.getOrderNumber());
            }
        }

        log.info("Payment processed successfully: {}", transactionRef);

        return transactionRef;
    }

    @Override
    @Transactional
    public void sendInvoice(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı"));

        String customerEmail = appointment.getCustomer().getEmail();
        String customerName = appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName();

        // HTML Fatura oluştur
        String subject = "NSL Motors - Faturanız";
        String htmlMessage = buildInvoiceHtml(appointment, customerName);

        // PDF oluştur
        byte[] pdfContent = pdfService.generatePdfFromHtml(htmlMessage);

        // Generate PDF filename
        String invoiceNumber = appointment.getTransactionReference() != null
                ? appointment.getTransactionReference()
                : "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String pdfFileName = "NSL_Motors_Fatura_" + invoiceNumber + ".pdf";

        // Mail'i HTML body ve PDF attachment ile gönder
        mailService.sendHtmlMessageWithPdfAttachment(customerEmail, subject, htmlMessage,
                pdfContent, pdfFileName);

        log.info("Invoice sent to: {} with PDF attachment: {}", customerEmail, pdfFileName);
    }

    private Order createOrderFromAppointment(Appointment appointment, String transactionRef) {
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
        order.setStatus("ONAYLANDI"); // Payment tamamlandığında otomatik onaylandı
        order.setAppointment(appointment);

        // Order'ı kaydet (ID oluşsun)
        Order savedOrder = orderRepository.save(order);

        // Invoice oluştur ve Order'a bağla
        Invoice invoice = createInvoiceForOrder(savedOrder, appointment);
        savedOrder.setInvoice(invoice);

        // Order'ı tekrar kaydet - cascade ile Invoice otomatik kaydedilecek
        orderRepository.save(savedOrder);

        log.info("Order and Invoice created: Order #{}", savedOrder.getOrderNumber());

        return savedOrder;
    }

    private Invoice createInvoiceForOrder(Order order, Appointment appointment) {
        Invoice invoice = new Invoice();

        // @MapsId kullanıldığı için Invoice'ın ID'si Order'ın ID'si ile aynı olacak
        // ID'yi manuel set etmiyoruz, Hibernate @MapsId ile otomatik hallediyor
        // Order'a set edip Order'ı kaydettiğimizde cascade ile Invoice da kaydedilecek

        // Fiyat hesaplama
        BigDecimal subtotalAmount = appointment.getPrice() != null ? appointment.getPrice() : BigDecimal.ZERO;
        BigDecimal taxRate = new BigDecimal("0.20"); // %20 KDV
        BigDecimal taxAmount = subtotalAmount.multiply(taxRate);
        BigDecimal totalAmount = subtotalAmount.add(taxAmount);

        // Invoice alanlarını doldur
        invoice.setOrder(order);
        // invoice.setId() ÇAĞRILMAMALI - @MapsId otomatik hallediyor
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setPaymentDate(LocalDateTime.now()); // Ödeme yapıldığı için payment date set ediliyor
        invoice.setSubtotalAmount(subtotalAmount);
        invoice.setTaxRate(taxRate);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalAmount(totalAmount);
        invoice.setPaymentMethod("CREDIT_CARD");
        invoice.setStatus("PAID"); // Ödeme yapıldı

        return invoice;
    }

    private String buildInvoiceHtml(Appointment appointment, String customerName) {
        try {
            // Load HTML template
            org.springframework.core.io.Resource resource = new org.springframework.core.io.ClassPathResource(
                    "templates/emails/invoice-email.html");
            String htmlTemplate = new String(resource.getInputStream().readAllBytes(),
                    java.nio.charset.StandardCharsets.UTF_8);
            // Calculate amounts
            BigDecimal subtotal = appointment.getPrice() != null ? appointment.getPrice() : BigDecimal.ZERO;
            BigDecimal taxRate = new BigDecimal("0.20");
            BigDecimal taxAmount = subtotal.multiply(taxRate);
            BigDecimal totalAmount = subtotal.add(taxAmount);
            // Format date and time
            String appointmentDate = appointment.getAppointmentDate().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String appointmentTime = appointment.getAppointmentTime().format(
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            String invoiceDate = LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            // Generate invoice number (using transaction reference or generating new one)
            String invoiceNumber = appointment.getTransactionReference() != null
                    ? appointment.getTransactionReference()
                    : "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            // Replace placeholders
            String htmlContent = htmlTemplate
                    .replace("{{customerName}}", customerName)
                    .replace("{{customerEmail}}", appointment.getCustomer().getEmail())
                    .replace("{{invoiceNumber}}", invoiceNumber)
                    .replace("{{invoiceDate}}", invoiceDate)
                    .replace("{{appointmentDate}}", appointmentDate)
                    .replace("{{appointmentTime}}", appointmentTime)
                    .replace("{{serviceName}}", "Performans Tuning")
                    .replace("{{carModel}}", appointment.getCar().getMake() + " " + appointment.getCar().getModel())
                    .replace("{{stageName}}", "Stage " + appointment.getSelectedStage())
                    .replace("{{servicePrice}}", String.format("%,.2f", subtotal))
                    .replace("{{subtotal}}", String.format("%,.2f", subtotal))
                    .replace("{{taxAmount}}", String.format("%,.2f", taxAmount))
                    .replace("{{totalAmount}}", String.format("%,.2f", totalAmount))
                    .replace("{{shopName}}", appointment.getShop().getName())
                    .replace("{{shopAddress}}",
                            appointment.getShop().getAddress() + ", " + appointment.getShop().getCity())
                    .replace("{{technicianName}}", appointment.getTechnician().getFirstName() + " "
                            + appointment.getTechnician().getLastName());
            return htmlContent;
        } catch (Exception e) {
            log.error("Error building invoice HTML: {}", e.getMessage(), e);
            throw new RuntimeException("Fatura HTML oluşturulamadı: " + e.getMessage(), e);
        }
    }
}
