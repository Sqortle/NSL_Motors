package com.ims.nslmotors.controller.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class EmployeeWebController {

    @GetMapping
    public String employeeDashboard(Model model, HttpSession session) {
        // Giriş kontrolü
        Boolean employeeLoggedIn = (Boolean) session.getAttribute("employeeLoggedIn");
        if (employeeLoggedIn == null || !employeeLoggedIn) {
            return "redirect:/admin/auth/login";
        }
        
        model.addAttribute("title", "Çalışan Paneli");
        model.addAttribute("employeeEmail", session.getAttribute("employeeEmail"));
        model.addAttribute("employeeRole", session.getAttribute("employeeRole"));
        return "employee/dashboard";
    }
}

