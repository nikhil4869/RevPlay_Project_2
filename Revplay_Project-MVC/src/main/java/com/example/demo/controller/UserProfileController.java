package com.example.demo.controller;

import com.example.demo.dto.music.UserProfileDTO;
import com.example.demo.service.UserApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserProfileController {

    private final UserApiService userApiService;

    public UserProfileController(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        String token = (String) session.getAttribute("JWT_TOKEN");

        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }

        try {
            UserProfileDTO profile = userApiService.getProfile(token);
            model.addAttribute("profile", profile);
            
            // Pass any status messages from session
            model.addAttribute("successMessage", session.getAttribute("profileSuccess"));
            model.addAttribute("errorMessage", session.getAttribute("profileError"));
            session.removeAttribute("profileSuccess");
            session.removeAttribute("profileError");
            
            return "user/profile";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching profile: " + e.getMessage());
            return "user/profile";
        }
    }

    @org.springframework.web.bind.annotation.PostMapping("/profile/update")
    public String updateProfile(UserProfileDTO dto, HttpSession session) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token == null) return "redirect:/login";

        try {
            userApiService.updateProfile(dto, token);
            session.setAttribute("profileSuccess", "Profile updated successfully!");
        } catch (Exception e) {
            session.setAttribute("profileError", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @org.springframework.web.bind.annotation.PostMapping("/profile/image")
    public String uploadImage(@org.springframework.web.bind.annotation.RequestParam("image") org.springframework.web.multipart.MultipartFile image, HttpSession session) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token == null) return "redirect:/login";

        try {
            userApiService.uploadProfileImage(image, token);
            session.setAttribute("profileSuccess", "Profile image updated!");
        } catch (Exception e) {
            session.setAttribute("profileError", "Error uploading image: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @org.springframework.web.bind.annotation.PostMapping("/profile/deactivate")
    public String deactivateAccount(HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token == null) return "redirect:/login";

        try {
            userApiService.deactivateAccount(token);
            session.invalidate(); // Logout after deactivation
            redirectAttributes.addFlashAttribute("success", "Account deactivated successfully. You can reactivate using Forgot Password.");
            return "redirect:/login";
        } catch (Exception e) {
            session.setAttribute("profileError", "Error deactivating account: " + e.getMessage());
            return "redirect:/user/profile";
        }
    }
    
}
