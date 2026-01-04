package com.ims.nslmotors.services.impl;

import com.ims.nslmotors.services.IMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements IMailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationCode(String toEmail, String verificationCode, String purpose) {
        try {
            log.debug("Email gönderiliyor - To: {}, From: {}, Purpose: {}", toEmail, fromEmail, purpose);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);

            String subject = getSubjectForPurpose(purpose);
            String body = getBodyForPurpose(purpose, verificationCode);

            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email başarıyla gönderildi - To: {}", toEmail);
        } catch (org.springframework.mail.MailAuthenticationException e) {
            log.error("Mail kimlik doğrulama hatası - From: {}, Detay: {}", fromEmail, e.getMessage(), e);
            throw new RuntimeException("Email gönderme hatası: Mail sunucu kimlik doğrulaması başarısız. " +
                    "Lütfen şunları kontrol edin:\n" +
                    "1. Gmail hesabınızda 2 Adımlı Doğrulama açık mı?\n" +
                    "2. App Password doğru oluşturuldu mu? (16 karakter, boşluksuz)\n" +
                    "3. application.properties'te spring.mail.password doğru mu?\n" +
                    "Hata detayı: " + e.getMessage(), e);
        } catch (org.springframework.mail.MailException e) {
            log.error("Mail gönderme hatası - To: {}, Detay: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException(
                    "Email gönderilemedi: " + e.getMessage() + ". Lütfen mail ayarlarını kontrol edin.", e);
        } catch (Exception e) {
            log.error("Beklenmeyen mail hatası - To: {}, Detay: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Email gönderme sırasında beklenmeyen bir hata oluştu: " + e.getMessage(), e);
        }
    }

    private String getSubjectForPurpose(String purpose) {
        return switch (purpose) {
            case "LOGIN" -> "NSL Motors - Giriş Doğrulama Kodu";
            case "ADMIN_LOGIN" -> "NSL Motors - Admin Giriş Doğrulama Kodu";
            case "EMPLOYEE_LOGIN" -> "NSL Motors - Çalışan Giriş Doğrulama Kodu";
            case "REGISTRATION" -> "NSL Motors - Kayıt Doğrulama Kodu";
            case "ORDER_COMPLETION" -> "NSL Motors - Sipariş Tamamlanma Doğrulama Kodu";
            default -> "NSL Motors - Doğrulama Kodu";
        };
    }

    private String getBodyForPurpose(String purpose, String code) {
        String actionText = switch (purpose) {
            case "LOGIN" -> "giriş yapmak için";
            case "ADMIN_LOGIN" -> "admin paneline giriş yapmak için";
            case "EMPLOYEE_LOGIN" -> "çalışan paneline giriş yapmak için";
            case "REGISTRATION" -> "kaydınızı tamamlamak için";
            case "ORDER_COMPLETION" -> "siparişinizi tamamlamak için";
            default -> "işleminizi tamamlamak için";
        };

        return String.format(
                "Merhaba,\n\n" +
                        "%s doğrulama kodunuz:\n\n" +
                        "%s\n\n" +
                        "Bu kod 10 dakika geçerlidir.\n\n" +
                        "Eğer bu işlemi siz yapmadıysanız, lütfen bu e-postayı görmezden gelin.\n\n" +
                        "Saygılarımızla,\n" +
                        "NSL Motors Ekibi",
                actionText, code);
    }

    @Override
    public void sendContactMessage(String fromEmail, String name, String message) {
        try {
            log.debug("İletişim mesajı gönderiliyor - From: {}, Name: {}", fromEmail, name);

            // Load HTML template
            String htmlTemplate = loadEmailTemplate("contact-email.html");

            // Replace placeholders
            String htmlContent = htmlTemplate
                    .replace("{{senderName}}", name)
                    .replace("{{senderEmail}}", fromEmail)
                    .replace("{{messageContent}}", message)
                    .replace("{{sendDate}}", java.time.LocalDateTime.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

            // Send HTML email
            sendHtmlMessage("imsbusiness00@gmail.com",
                    "NSL Motors - İletişim Formu Mesajı: " + name,
                    htmlContent);

            log.info("İletişim mesajı başarıyla gönderildi - From: {}", fromEmail);
        } catch (org.springframework.mail.MailAuthenticationException e) {
            log.error("Mail kimlik doğrulama hatası - Detay: {}", e.getMessage(), e);
            throw new RuntimeException("Email gönderme hatası: Mail sunucu kimlik doğrulaması başarısız. " +
                    "Lütfen mail ayarlarını kontrol edin. Hata detayı: " + e.getMessage(), e);
        } catch (org.springframework.mail.MailException e) {
            log.error("Mail gönderme hatası - From: {}, Detay: {}", fromEmail, e.getMessage(), e);
            throw new RuntimeException(
                    "Email gönderilemedi: " + e.getMessage() + ". Lütfen mail ayarlarını kontrol edin.", e);
        } catch (Exception e) {
            log.error("Beklenmeyen mail hatası - From: {}, Detay: {}", fromEmail, e.getMessage(), e);
            throw new RuntimeException("Email gönderme sırasında beklenmeyen bir hata oluştu: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Simple email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending simple email: {}", e.getMessage());
            throw new RuntimeException("Email gönderilemedi: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendHtmlMessage(String to, String subject, String htmlContent) {
        try {
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
                    mailSender.createMimeMessage(), true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(helper.getMimeMessage());
            log.info("HTML email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending HTML email: {}", e.getMessage());
            throw new RuntimeException("HTML Email gönderilemedi: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendHtmlMessageWithPdfAttachment(String to, String subject, String htmlContent,
            byte[] pdfContent, String pdfFileName) {
        try {
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
                    mailSender.createMimeMessage(), true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            // Add PDF attachment using ByteArrayResource
            org.springframework.core.io.ByteArrayResource pdfResource = new org.springframework.core.io.ByteArrayResource(
                    pdfContent);
            helper.addAttachment(pdfFileName, pdfResource);

            mailSender.send(helper.getMimeMessage());
            log.info("HTML email with PDF attachment sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending HTML email with PDF attachment: {}", e.getMessage());
            throw new RuntimeException("PDF ekli email gönderilemedi: " + e.getMessage(), e);
        }
    }

    // Helper method to load email templates
    private String loadEmailTemplate(String templateName) {
        try {
            org.springframework.core.io.Resource resource = new org.springframework.core.io.ClassPathResource(
                    "templates/emails/" + templateName);
            return new String(resource.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Email template yüklenemedi: {}", templateName, e);
            throw new RuntimeException("Email template yüklenemedi: " + templateName, e);
        }
    }
}
