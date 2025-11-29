package com.ims.nslmotors.controller.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @GetMapping
    public String adminDashboard(Model model, HttpSession session) {
        // Giriş kontrolü
        Boolean adminLoggedIn = (Boolean) session.getAttribute("adminLoggedIn");
        if (adminLoggedIn == null || !adminLoggedIn) {
            return "redirect:/admin/auth/login";
        }
        
        model.addAttribute("title", "Admin Paneli");
        model.addAttribute("adminEmail", session.getAttribute("adminEmail"));
        return "admin/dashboard";
    }
}

