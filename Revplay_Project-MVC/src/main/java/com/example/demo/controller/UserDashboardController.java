package com.example.demo.controller;

import com.example.demo.dto.user.UserDashboardDTO;
import com.example.demo.service.UserApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    private final UserApiService userApiService;

    public UserDashboardController(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        
        // Retrieve the JWT token from the session, matching the Artist controllers setup.
        String token = (String) session.getAttribute("JWT_TOKEN");
        
        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }

        try {
            // Fetch dashboard data via our API service bridging to port 8080.
            UserDashboardDTO dashboardData = userApiService.getDashboardData(token);
            com.example.demo.dto.music.UserProfileDTO profileData = userApiService.getProfile(token);
            java.util.List<com.example.demo.dto.music.HistoryDTO> recentHistory = userApiService.getRecentHistory(token);
            
            // Pass the model exactly as expected by dashboard.html frontend layout template.
            model.addAttribute("dashboard", dashboardData);
            model.addAttribute("profile", profileData);
            model.addAttribute("recentHistory", recentHistory);

            return "user/dashboard";

        } catch (Exception e) {
            // Log or handle any connection / parse issues with the backend.
            model.addAttribute("errorMessage", "Error connecting to the backend core. " + e.getMessage());
            return "user/dashboard";
        }
    }
}
