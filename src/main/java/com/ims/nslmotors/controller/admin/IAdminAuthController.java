package com.ims.nslmotors.controller.admin;

import com.ims.nslmotors.dto.admin.DtoAdminLogin;
import com.ims.nslmotors.dto.admin.DtoAdminVerification;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface IAdminAuthController {
    
    @GetMapping("/login")
    String showLoginPage(Model model);
    
    @PostMapping("/login")
    String handleLogin(DtoAdminLogin loginDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, jakarta.servlet.http.HttpSession session);
    
    @GetMapping("/verify")
    String showVerifyPage(Model model, jakarta.servlet.http.HttpSession session);
    
    @PostMapping("/verify")
    String handleVerify(@Valid DtoAdminVerification verificationDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, jakarta.servlet.http.HttpSession session);
    
    @GetMapping("/logout")
    String logout(jakarta.servlet.http.HttpSession session);
}

