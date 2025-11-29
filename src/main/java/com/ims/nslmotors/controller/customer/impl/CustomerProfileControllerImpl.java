package com.ims.nslmotors.controller.customer.impl;

import com.ims.nslmotors.controller.customer.ICustomerProfileController;
import com.ims.nslmotors.dto.customer.DtoCustomerPasswordChange;
import com.ims.nslmotors.dto.customer.DtoCustomerProfile;
import com.ims.nslmotors.dto.customer.DtoCustomerProfileUpdate;
import com.ims.nslmotors.services.customer.ICustomerProfileService;
import jakarta.servlet.http.HttpSession;
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

@Controller
@RequestMapping("/customer/profile")
@RequiredArgsConstructor
public class CustomerProfileControllerImpl implements ICustomerProfileController {

    private final ICustomerProfileService profileService;

    @GetMapping
    public String showProfilePage(HttpSession session, Model model) {
        // Session'dan kullanıcı ID'sini al
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            return "redirect:/customer/auth/login";
        }

        try {
            DtoCustomerProfile profile = profileService.getProfile(customerId);
            model.addAttribute("profile", profile);
            
            // Form DTO'larını hazırla
            if (!model.containsAttribute("profileUpdate")) {
                DtoCustomerProfileUpdate profileUpdate = new DtoCustomerProfileUpdate();
                profileUpdate.setFirstName(profile.getFirstName());
                profileUpdate.setLastName(profile.getLastName());
                profileUpdate.setEmail(profile.getEmail());
                profileUpdate.setPhoneNumber(profile.getPhoneNumber());
                model.addAttribute("profileUpdate", profileUpdate);
            }
            
            if (!model.containsAttribute("passwordChange")) {
                model.addAttribute("passwordChange", new DtoCustomerPasswordChange());
            }
            
            model.addAttribute("title", "Profilim");
            return "customer/profile";
        } catch (RuntimeException e) {
            return "redirect:/customer/auth/login";
        }
    }

    @PostMapping("/update")
    public String updateProfile(
            @Valid @ModelAttribute("profileUpdate") DtoCustomerProfileUpdate profileUpdate,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            return "redirect:/customer/auth/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profileUpdate", bindingResult);
            redirectAttributes.addFlashAttribute("profileUpdate", profileUpdate);
            return "redirect:/customer/profile";
        }

        try {
            profileService.updateProfile(customerId, profileUpdate);
            redirectAttributes.addFlashAttribute("successMessage", "Profil bilgileriniz başarıyla güncellendi.");
            
            // Session'daki kullanıcı bilgilerini güncelle
            DtoCustomerProfile updatedProfile = profileService.getProfile(customerId);
            session.setAttribute("customerName", updatedProfile.getFirstName() + " " + updatedProfile.getLastName());
            session.setAttribute("customerEmail", updatedProfile.getEmail());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("profileUpdate", profileUpdate);
        }
        
        return "redirect:/customer/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @Valid @ModelAttribute("passwordChange") DtoCustomerPasswordChange passwordChange,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId == null) {
            return "redirect:/customer/auth/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordChange", bindingResult);
            redirectAttributes.addFlashAttribute("passwordChange", passwordChange);
            return "redirect:/customer/profile";
        }

        try {
            profileService.changePassword(customerId, passwordChange);
            redirectAttributes.addFlashAttribute("successMessage", "Şifreniz başarıyla değiştirildi.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("passwordChange", passwordChange);
        }
        
        return "redirect:/customer/profile";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}

