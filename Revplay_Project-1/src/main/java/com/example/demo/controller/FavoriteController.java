package com.example.demo.controller;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    //  add favorite
    @PostMapping("/{songId}")
    public String addFavorite(@PathVariable Long songId) {
        favoriteService.addFavorite(songId);
        return "Added to favorites";
    }

    //  remove favorite
    @DeleteMapping("/{songId}")
    public String removeFavorite(@PathVariable Long songId) {
        favoriteService.removeFavorite(songId);
        return "Removed from favorites";
    }

    //  view favorites
    @GetMapping
    public List<SongDTO> getMyFavorites() {
        return favoriteService.getMyFavorites();
    }
}
