package com.ims.nslmotors.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class StaticPageController {

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Hakkımızda");
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("title", "İletişim");
        return "contact";
    }
}

