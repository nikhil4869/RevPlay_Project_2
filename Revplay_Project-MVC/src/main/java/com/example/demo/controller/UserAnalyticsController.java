package com.example.demo.controller;

import com.example.demo.dto.analytics.UserAnalyticsDTO;
import com.example.demo.dto.music.UserProfileDTO;
import com.example.demo.service.UserApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/analytics")
public class UserAnalyticsController {

    private final UserApiService userApiService;

    public UserAnalyticsController(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @GetMapping
    public String showAnalytics(HttpSession session, Model model) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token == null) return "redirect:/login";

        try {
            UserAnalyticsDTO analytics = userApiService.getUserAnalytics(token);
            UserProfileDTO profile = userApiService.getProfile(token);
            
            model.addAttribute("analytics", analytics);
            model.addAttribute("profile", profile);
            return "user/analytics";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching analytics: " + e.getMessage());
            model.addAttribute("analytics", new UserAnalyticsDTO()); // Provide empty object to prevent Thymeleaf crash
            return "user/analytics";
        }
    }
}
