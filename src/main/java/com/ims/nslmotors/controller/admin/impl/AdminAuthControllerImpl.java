package com.ims.nslmotors.controller.admin.impl;

import com.ims.nslmotors.controller.admin.IAdminAuthController;
import com.ims.nslmotors.dto.admin.DtoAdminLogin;
import com.ims.nslmotors.dto.admin.DtoAdminVerification;
import com.ims.nslmotors.model.Employee;
import com.ims.nslmotors.repository.admin.AdminEmployeeRepository;
import com.ims.nslmotors.services.admin.IAdminAuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthControllerImpl implements IAdminAuthController {

    private final IAdminAuthService adminAuthService;
    private final AdminEmployeeRepository employeeRepository;
    
    // Master kullanıcı bilgileri - application-secrets.properties dosyasından okunur
    // Bu dosya .gitignore'a eklenmiştir ve GitHub'a push edilmez
    @Value("${master.admin.email:master}")
    private String masterEmail;
    
    @Value("${master.admin.password:master444}")
    private String masterPassword;
    
    /**
     * Master kullanıcı kontrolü - properties dosyasından okunan bilgilerle kontrol eder
     */
    private boolean isMasterUser(String email, String password) {
        if (email == null || password == null) {
            return false;
        }
        // Email kontrolü (case-insensitive)
        if (!masterEmail.equalsIgnoreCase(email.trim())) {
            return false;
        }
        // Master şifresi kontrolü
        return masterPassword.equals(password);
    }

    @Override
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        if (!model.containsAttribute("loginDto")) {
            model.addAttribute("loginDto", new DtoAdminLogin());
        }
        model.addAttribute("title", "Admin Girişi");
        return "admin/login";
    }

    @Override
    @PostMapping("/login")
    public String handleLogin(
            DtoAdminLogin loginDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        // Master kullanıcı kontrolü - validation'dan önce kontrol et (master email formatı geçerli olmayabilir)
        // NOT: Master kullanıcı için @Valid annotation'ı kullanmıyoruz çünkü "master" geçerli bir email formatı değil
        if (isMasterUser(loginDto.getEmail(), loginDto.getPassword())) {
            // Master kullanıcı için direkt admin paneline yönlendir (verification code olmadan)
            session.setAttribute("adminLoggedIn", true);
            session.setAttribute("employeeLoggedIn", true);
            session.setAttribute("adminEmail", "master");
            session.setAttribute("employeeEmail", "master");
            session.setAttribute("employeeRole", "ADMIN");
            session.setAttribute("employeeFirstName", "Master");
            session.setAttribute("employeeLastName", "Admin");
            session.setAttribute("isMasterUser", true);
            redirectAttributes.addFlashAttribute("successMessage", "Master kullanıcı olarak giriş yapıldı.");
            return "redirect:/admin";
        }

        // Normal kullanıcılar için manuel validation
        if (loginDto.getEmail() == null || loginDto.getEmail().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email alanı zorunludur.");
            redirectAttributes.addFlashAttribute("loginDto", loginDto);
            return "redirect:/admin/auth/login";
        }
        if (!loginDto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Geçerli bir email adresi giriniz.");
            redirectAttributes.addFlashAttribute("loginDto", loginDto);
            return "redirect:/admin/auth/login";
        }
        if (loginDto.getPassword() == null || loginDto.getPassword().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Şifre alanı zorunludur.");
            redirectAttributes.addFlashAttribute("loginDto", loginDto);
            return "redirect:/admin/auth/login";
        }

        try {
            adminAuthService.initiateLogin(loginDto);
            // Doğrulama kodu gönderildi - email'i session'a kaydet ve verify sayfasına yönlendir
            session.setAttribute("pendingAdminEmail", loginDto.getEmail());
            redirectAttributes.addFlashAttribute("email", loginDto.getEmail());
            return "redirect:/admin/auth/verify";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("loginDto", loginDto);
            return "redirect:/admin/auth/login";
        }
    }

    @Override
    @GetMapping("/verify")
    public String showVerifyPage(Model model, HttpSession session) {
        if (!model.containsAttribute("verificationDto")) {
            DtoAdminVerification verificationDto = new DtoAdminVerification();
            // Session'dan email'i al
            String email = (String) session.getAttribute("pendingAdminEmail");
            if (email != null) {
                verificationDto.setEmail(email);
                model.addAttribute("email", email);
            }
            model.addAttribute("verificationDto", verificationDto);
        }
        model.addAttribute("title", "Admin Doğrulama");
        return "admin/verify";
    }

    @Override
    @PostMapping("/verify")
    public String handleVerify(
            @Valid DtoAdminVerification verificationDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        // Session'dan email'i al, eğer DTO'da yoksa
        String email = (String) session.getAttribute("pendingAdminEmail");
        if (email != null && (verificationDto.getEmail() == null || verificationDto.getEmail().isEmpty())) {
            verificationDto.setEmail(email);
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.verificationDto", bindingResult);
            redirectAttributes.addFlashAttribute("verificationDto", verificationDto);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/admin/auth/verify";
        }

        try {
            adminAuthService.verifyLogin(verificationDto.getEmail(), verificationDto.getVerificationCode());
            // Doğrulama başarılı - employee'yi bul ve role göre yönlendir
            Employee employee = employeeRepository.findByEmail(verificationDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
            
            session.removeAttribute("pendingAdminEmail");
            session.setAttribute("employeeLoggedIn", true);
            session.setAttribute("employeeEmail", verificationDto.getEmail());
            session.setAttribute("employeeId", employee.getId());
            session.setAttribute("employeeRole", employee.getRole());
            session.setAttribute("employeeFirstName", employee.getFirstName());
            session.setAttribute("employeeLastName", employee.getLastName());
            
            redirectAttributes.addFlashAttribute("successMessage", "Giriş başarılı! Hoş geldiniz.");
            
            // Role göre yönlendir
            String role = employee.getRole();
            if ("ADMIN".equals(role) || "OWNER".equals(role)) {
                session.setAttribute("adminLoggedIn", true);
                session.setAttribute("adminEmail", verificationDto.getEmail());
                if ("OWNER".equals(role)) {
                    session.setAttribute("isOwner", true);
                }
                return "redirect:/admin";
            } else {
                // EMPLOYEE, TECHNICIAN, MASTER vb. roller için
                return "redirect:/employee";
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("verificationDto", verificationDto);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/admin/auth/verify";
        }
    }

    @Override
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/auth/login";
    }
}

