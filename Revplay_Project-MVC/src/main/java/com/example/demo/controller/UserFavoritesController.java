package com.example.demo.controller;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.service.UserApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserFavoritesController {

    private final UserApiService userApiService;

    public UserFavoritesController(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @GetMapping("/favorites")
    public String showFavorites(HttpSession session, Model model) {
        String token = (String) session.getAttribute("JWT_TOKEN");

        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }

        try {
            List<SongDTO> songs = userApiService.getFavorites(token);
            model.addAttribute("songs", songs);
            model.addAttribute("isFavoritesPage", true);
            return "user/songs";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching favorites: " + e.getMessage());
            return "user/songs";
        }
    }
}
