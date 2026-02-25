package com.example.demo.controller;

import com.example.demo.dto.music.SongDTO;
<<<<<<< HEAD
import com.example.demo.entity.Song;
import com.example.demo.service.FavoriteService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
=======
import com.example.demo.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

<<<<<<< HEAD
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
=======
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
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
