package com.example.demo.controller;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/player")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/play/{songId}")
    public SongDTO playSong(@PathVariable Long songId) {
        return playerService.playSong(songId);
    }

    @GetMapping("/history")
    public List<SongDTO> getMyHistory() {
        return playerService.getMyHistory();
    }
    
    // Recently played
    @GetMapping("/recent")
    public List<SongDTO> getRecentlyPlayed(
            @RequestParam(defaultValue = "10") int limit) {
        return playerService.getRecentlyPlayed(limit);
    }

    // Most played
    @GetMapping("/most-played")
    public List<SongDTO> getMostPlayed(
            @RequestParam(defaultValue = "5") int limit) {
        return playerService.getMostPlayedSongs(limit);
    }

    // Trending
    @GetMapping("/trending")
    public List<SongDTO> getTrending(
            @RequestParam(defaultValue = "5") int limit) {
        return playerService.getTrendingSongs(limit);
    }
}