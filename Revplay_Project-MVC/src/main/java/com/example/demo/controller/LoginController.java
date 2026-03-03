package com.example.demo.controller;

import com.example.demo.service.ApiService;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final ApiService apiService;

    public LoginController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session) {

        Map<String, String> response = apiService.login(email, password);

        String token = response.get("token");
        String role = response.get("role");

        session.setAttribute("JWT_TOKEN", token);
        session.setAttribute("ROLE", role);

        if ("ARTIST".equalsIgnoreCase(role)) {
            return "redirect:/artist/main";
        } else {
            return "redirect:/user/dashboard";
        }
    }
    
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam Map<String, String> formData,
                           RedirectAttributes redirectAttributes) {

        apiService.register(formData);

        redirectAttributes.addFlashAttribute(
                "success",
                "Registration successful! Please login."
        );

        return "redirect:/login";
    }
    
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam Map<String, String> formData,
                                 RedirectAttributes redirectAttributes) {

        apiService.resetPassword(formData);

        redirectAttributes.addFlashAttribute(
                "success",
                "Password reset successful! Please login."
        );

        return "redirect:/login";
    }
    
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
        
}