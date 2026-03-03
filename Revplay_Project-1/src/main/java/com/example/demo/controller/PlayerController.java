package com.example.demo.controller;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.service.PlayerService;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/player")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/play/{songId}")
    public String playSong(@PathVariable Long songId) {
        playerService.playSong(songId);
        return "Song play recorded";
    }
    
 // Trending
    @GetMapping("/trending")
    public List<SongDTO> getTrending(
            @RequestParam(defaultValue = "5") int limit) {
        return playerService.getTrendingSongs(limit);
    }
}