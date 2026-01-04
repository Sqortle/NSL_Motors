package com.ims.nslmotors.controller.web;

import com.ims.nslmotors.services.IMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class StaticPageController {

    private final IMailService mailService;

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

    @PostMapping("/contact")
    public String handleContactForm(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("message") String message,
            RedirectAttributes redirectAttributes) {
        try {
            mailService.sendContactMessage(email, name, message);
            redirectAttributes.addFlashAttribute("success", "Mesajınız başarıyla gönderildi! En kısa sürede size dönüş yapacağız.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Mesaj gönderilirken bir hata oluştu. Lütfen daha sonra tekrar deneyin.");
        }
        return "redirect:/contact";
    }
}

