package com.example.demo.controller;

import com.example.demo.dto.music.AlbumDTO;
import com.example.demo.service.UserApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserAlbumController {

    private final UserApiService userApiService;

    public UserAlbumController(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @GetMapping("/albums")
    public String showAlbums(HttpSession session, Model model) {
        String token = (String) session.getAttribute("JWT_TOKEN");

        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }

        try {
            List<AlbumDTO> albums = userApiService.getAllAlbums(token);
            model.addAttribute("albums", albums);
            return "user/albums";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching albums: " + e.getMessage());
            return "user/albums";
        }
    }

    @GetMapping("/albums/{id}/songs")
    public String getAlbumSongs(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token == null || token.isEmpty()) return "redirect:/login";

        try {
            AlbumDTO album = userApiService.getAlbumDetails(id, token);
            List<com.example.demo.dto.music.SongDTO> songs = userApiService.getAlbumSongs(id, token);
            model.addAttribute("album", album);
            model.addAttribute("songs", songs);
            return "user/album-songs";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching album songs: " + e.getMessage());
            return "user/albums";
        }
    }
}
