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
public class UserSongsController {

    private final UserApiService userApiService;

    public UserSongsController(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @GetMapping("/songs")
    public String showSongs(HttpSession session, Model model) {
        String token = (String) session.getAttribute("JWT_TOKEN");

        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }

        try {
            List<SongDTO> songs = userApiService.getAllSongs(token);
            model.addAttribute("songs", songs);
            
            // Add filter lists
            model.addAttribute("genres", userApiService.getAllGenres(token));
            model.addAttribute("years", userApiService.getAllYears(token));
            model.addAttribute("artists", userApiService.getAllArtists(token));
            model.addAttribute("albums", userApiService.getAllAlbums(token));

            return "user/songs";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching songs: " + e.getMessage());
            return "user/songs";
        }
    }
}
