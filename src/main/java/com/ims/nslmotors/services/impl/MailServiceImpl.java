package com.ims.nslmotors.services.impl;

import com.ims.nslmotors.services.IMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements IMailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationCode(String toEmail, String verificationCode, String purpose) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            
            String subject = getSubjectForPurpose(purpose);
            String body = getBodyForPurpose(purpose, verificationCode);
            
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } catch (org.springframework.mail.MailAuthenticationException e) {
            throw new RuntimeException("Email gönderme hatası: Mail sunucu kimlik doğrulaması başarısız. Lütfen application.properties dosyasındaki mail ayarlarını kontrol edin.", e);
        } catch (org.springframework.mail.MailException e) {
            throw new RuntimeException("Email gönderilemedi: " + e.getMessage() + ". Lütfen mail ayarlarını kontrol edin.", e);
        } catch (Exception e) {
            throw new RuntimeException("Email gönderme sırasında beklenmeyen bir hata oluştu: " + e.getMessage(), e);
        }
    }

    private String getSubjectForPurpose(String purpose) {
        return switch (purpose) {
            case "LOGIN" -> "NSL Motors - Giriş Doğrulama Kodu";
            case "REGISTRATION" -> "NSL Motors - Kayıt Doğrulama Kodu";
            case "ORDER_COMPLETION" -> "NSL Motors - Sipariş Tamamlanma Doğrulama Kodu";
            default -> "NSL Motors - Doğrulama Kodu";
        };
    }

    private String getBodyForPurpose(String purpose, String code) {
        String actionText = switch (purpose) {
            case "LOGIN" -> "giriş yapmak için";
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
            actionText, code
        );
    }
}

