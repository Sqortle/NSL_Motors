package com.ims.nslmotors.controller.admin.impl;

import com.ims.nslmotors.controller.admin.IAdminAuthController;
import com.ims.nslmotors.dto.admin.DtoAdminLogin;
import com.ims.nslmotors.services.admin.IAdminAuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            @Valid DtoAdminLogin loginDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginDto", bindingResult);
            redirectAttributes.addFlashAttribute("loginDto", loginDto);
            return "redirect:/admin/auth/login";
        }

        try {
            adminAuthService.login(loginDto);
            // Giriş başarılı - session'a admin bilgilerini ekle
            session.setAttribute("adminLoggedIn", true);
            session.setAttribute("adminEmail", loginDto.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", "Giriş başarılı! Hoş geldiniz.");
            return "redirect:/admin";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("loginDto", loginDto);
            return "redirect:/admin/auth/login";
        }
    }

    @Override
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/auth/login";
    }
}

