package com.example.demo.controller;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.Song;
import com.example.demo.service.FavoriteService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{songId}")
    public String add(@PathVariable Long songId) {
        favoriteService.addToFavorites(songId);
        return "Added to favorites";
    }

    @DeleteMapping("/{songId}")
    public String remove(@PathVariable Long songId) {
        favoriteService.removeFromFavorites(songId);
        return "Removed from favorites";
    }

    @GetMapping
    public List<SongDTO> getMyFavorites() {

        List<Song> songs = favoriteService.getMyFavorites();

        return songs.stream()
                .map(song -> new SongDTO(
                        song.getId(),
                        song.getTitle(),
                        song.getGenre(),
                        song.getDuration(),
                        song.getAudioPath(),
                        song.getCoverImage(),
                        song.getArtist().getName()
                ))
                .collect(Collectors.toList());
    }
}