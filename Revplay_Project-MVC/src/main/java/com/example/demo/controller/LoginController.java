package com.example.demo.controller;

import com.example.demo.dto.ForgotPasswordRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.ApiService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class LoginController {

    private final ApiService apiService;

    public LoginController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            Map<String, String> response = apiService.login(loginRequest.getEmail(), loginRequest.getPassword());

            String token = response.get("token");
            String role = response.get("role");

            session.setAttribute("JWT_TOKEN", token);
            session.setAttribute("ROLE", role);

            if ("ARTIST".equalsIgnoreCase(role)) {
                return "redirect:/artist/main";
            } else {
                return "redirect:/user/dashboard";
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String displayError = "Invalid email or password";

            if (errorMessage != null) {
                if (errorMessage.contains("Email not registered")) {
                    displayError = "No account found with that email ID";
                } else if (errorMessage.contains("Invalid credentials")) {
                    displayError = "Incorrect password";
                } else if (errorMessage.contains("Account is deactivated")) {
                    displayError = "Account is deactivated. Use forgot password to reactivate.";
                }
            }

            model.addAttribute("error", displayError);
            return "login";
        }
    }
    
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.registerRequest", "Passwords do not match");
            return "register";
        }

        try {
            Map<String, String> formData = new HashMap<>();
            formData.put("name", registerRequest.getName());
            formData.put("email", registerRequest.getEmail());
            formData.put("password", registerRequest.getPassword());
            formData.put("confirmPassword", registerRequest.getConfirmPassword());
            formData.put("role", registerRequest.getRole());
            formData.put("dateOfBirth", registerRequest.getDateOfBirth().toString());

            apiService.register(formData);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Registration successful! Please login."
            );

            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }
    
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        if (!model.containsAttribute("forgotPasswordRequest")) {
            model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        }
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @ModelAttribute("forgotPasswordRequest") ForgotPasswordRequest forgotPasswordRequest,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "forgot-password";
        }

        try {
            Map<String, String> formData = new HashMap<>();
            formData.put("email", forgotPasswordRequest.getEmail());
            formData.put("newPassword", forgotPasswordRequest.getNewPassword());
            formData.put("dateOfBirth", forgotPasswordRequest.getDateOfBirth().toString());

            apiService.resetPassword(formData);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Password reset successful! Please login."
            );

            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Password reset failed: " + e.getMessage());
            return "redirect:/forgot-password";
        }
    }
    
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
        
}