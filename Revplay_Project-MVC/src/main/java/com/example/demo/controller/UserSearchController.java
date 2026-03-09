package com.example.demo.controller;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.service.UserApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserSearchController {

    private final UserApiService userApiService;

    public UserSearchController(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @GetMapping("/search")
    public String searchSongs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long artistId,
            @RequestParam(required = false) Long albumId,
            HttpSession session, Model model) {
        String token = (String) session.getAttribute("JWT_TOKEN");

        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }

        try {
            List<SongDTO> songs;
            if (keyword != null && !keyword.isEmpty()) {
                songs = userApiService.searchSongs(keyword, token);
                model.addAttribute("keyword", keyword);
            } else if (genre != null && !genre.isEmpty()) {
                songs = userApiService.getSongsByGenre(genre, token);
                model.addAttribute("activeGenre", genre);
            } else if (year != null) {
                songs = userApiService.getSongsByYear(year, token);
                model.addAttribute("activeYear", year);
            } else if (artistId != null) {
                songs = userApiService.getSongsByArtist(artistId, token);
                model.addAttribute("activeArtistId", artistId);
            } else if (albumId != null) {
                songs = userApiService.getSongsByAlbum(albumId, token);
                model.addAttribute("activeAlbumId", albumId);
            } else {
                songs = userApiService.getAllSongs(token);
            }

            model.addAttribute("songs", songs);
            
            // Add filter lists
            model.addAttribute("genres", userApiService.getAllGenres(token));
            model.addAttribute("years", userApiService.getAllYears(token));
            model.addAttribute("artists", userApiService.getAllArtists(token));
            model.addAttribute("albums", userApiService.getAllAlbums(token));

            return "user/songs";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error searching songs: " + e.getMessage());
            return "user/songs";
        }
    }
}
