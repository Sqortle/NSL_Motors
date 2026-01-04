package com.ims.nslmotors.controller.customer.impl;

import com.ims.nslmotors.controller.customer.ICustomerAuthController;
import com.ims.nslmotors.dto.customer.DtoCustomerLogin;
import com.ims.nslmotors.dto.customer.DtoCustomerProfile;
import com.ims.nslmotors.dto.customer.DtoCustomerRegistirationIU;
import com.ims.nslmotors.dto.customer.DtoCustomerVerification;
import com.ims.nslmotors.services.customer.ICustomerAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/auth")
@RequiredArgsConstructor
public class CustomerAuthControllerImpl implements ICustomerAuthController {

    private final ICustomerAuthService customerAuthService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if (!model.containsAttribute("registrationDto")) {
            model.addAttribute("registrationDto", new DtoCustomerRegistirationIU());
        }
        model.addAttribute("title", "Kayıt Ol");
        return "customer/register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @Valid @ModelAttribute("registrationDto") DtoCustomerRegistirationIU registrationDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // Şifre eşleşme kontrolü
        if (registrationDto.getPassword() != null && registrationDto.getConfirmPassword() != null) {
            if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Şifreler eşleşmiyor.");
            }
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registrationDto", bindingResult);
            redirectAttributes.addFlashAttribute("registrationDto", registrationDto);
            return "redirect:/customer/auth/register";
        }

        try {
            customerAuthService.initiateRegistration(registrationDto);
            redirectAttributes.addFlashAttribute("email", registrationDto.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", 
                "Kayıt işlemi başarılı! Doğrulama kodu email adresinize gönderildi. Lütfen kontrol ediniz.");
            return "redirect:/customer/auth/verify-register";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("registrationDto", registrationDto);
            return "redirect:/customer/auth/register";
        }
    }

    @GetMapping("/verify-register")
    public String showRegisterVerificationPage(Model model) {
        if (!model.containsAttribute("verificationDto")) {
            DtoCustomerVerification verificationDto = new DtoCustomerVerification();
            if (model.containsAttribute("email")) {
                verificationDto.setEmail((String) model.asMap().get("email"));
            }
            model.addAttribute("verificationDto", verificationDto);
        }
        model.addAttribute("title", "Hesap Doğrulama");
        return "customer/verify-register";
    }

    @PostMapping("/verify-register")
    public String handleRegisterVerification(
            @Valid @ModelAttribute("verificationDto") DtoCustomerVerification verificationDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.verificationDto", bindingResult);
            redirectAttributes.addFlashAttribute("verificationDto", verificationDto);
            return "redirect:/customer/auth/verify-register";
        }

        try {
            DtoCustomerProfile profile = customerAuthService.verifyAndActivateAccount(verificationDto.getEmail(), verificationDto.getVerificationCode());
            // Session'a kullanıcı bilgilerini ekle (otomatik giriş)
            session.setAttribute("customerId", profile.getId());
            session.setAttribute("customerName", profile.getFirstName() + " " + profile.getLastName());
            session.setAttribute("customerEmail", profile.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", 
                "Hesabınız başarıyla doğrulandı! Hoş geldiniz.");
            return "redirect:/"; // Ana sayfaya yönlendir
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("verificationDto", verificationDto);
            return "redirect:/customer/auth/verify-register";
        }
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        if (!model.containsAttribute("loginDto")) {
            model.addAttribute("loginDto", new DtoCustomerLogin());
        }
        model.addAttribute("title", "Giriş Yap");
        return "customer/login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @Valid @ModelAttribute("loginDto") DtoCustomerLogin loginDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginDto", bindingResult);
            redirectAttributes.addFlashAttribute("loginDto", loginDto);
            return "redirect:/customer/auth/login";
        }

        try {
            customerAuthService.initiateLogin(loginDto);
            redirectAttributes.addFlashAttribute("email", loginDto.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", 
                "Doğrulama kodu email adresinize gönderildi. Lütfen kontrol ediniz.");
            return "redirect:/customer/auth/verify";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("loginDto", loginDto);
            return "redirect:/customer/auth/login";
        }
    }

    @GetMapping("/verify")
    public String showVerificationPage(Model model) {
        if (!model.containsAttribute("verificationDto")) {
            DtoCustomerVerification verificationDto = new DtoCustomerVerification();
            if (model.containsAttribute("email")) {
                verificationDto.setEmail((String) model.asMap().get("email"));
            }
            model.addAttribute("verificationDto", verificationDto);
        }
        model.addAttribute("title", "Doğrulama Kodu");
        return "customer/verify";
    }

    @PostMapping("/verify")
    public String handleVerification(
            @Valid @ModelAttribute("verificationDto") DtoCustomerVerification verificationDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.verificationDto", bindingResult);
            redirectAttributes.addFlashAttribute("verificationDto", verificationDto);
            return "redirect:/customer/auth/verify";
        }

        try {
            DtoCustomerProfile profile = customerAuthService.verifyLogin(verificationDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Giriş başarılı! Hoş geldiniz.");
            // Session'a kullanıcı bilgilerini ekle
            session.setAttribute("customerId", profile.getId());
            session.setAttribute("customerName", profile.getFirstName() + " " + profile.getLastName());
            session.setAttribute("customerEmail", profile.getEmail());
            return "redirect:/"; // Ana sayfaya yönlendir
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("verificationDto", verificationDto);
            return "redirect:/customer/auth/verify";
        }
    }
}
