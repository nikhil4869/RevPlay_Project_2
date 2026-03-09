package com.example.demo.controller;

import com.example.demo.dto.playlist.PlaylistDTO;
import com.example.demo.service.UserApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proxy")
public class UserProxyController {

    private final UserApiService userApiService;

    public UserProxyController(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @PostMapping("/play/{songId}")
    public ResponseEntity<?> recordPlay(@PathVariable Long songId, HttpSession session) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token == null) return ResponseEntity.status(401).body("Unauthorized");
        
        try {
            userApiService.recordPlay(songId, token);
            return ResponseEntity.ok("Play recorded");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/favorites/{songId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long songId, HttpSession session) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token == null) return ResponseEntity.status(401).body("Unauthorized");
        
        try {
            userApiService.addFavorite(songId, token);
            return ResponseEntity.ok("Added to favorites");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/favorites/{songId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long songId, HttpSession session) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token == null) return ResponseEntity.status(401).body("Unauthorized");
        
        try {
            userApiService.removeFavorite(songId, token);
            return ResponseEntity.ok("Removed from favorites");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/playlists/my")
    public ResponseEntity<?> getMyPlaylists(HttpSession session) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token == null) return ResponseEntity.status(401).body("Unauthorized");
        
        try {
            List<PlaylistDTO> playlists = userApiService.getMyPlaylists(token);
            return ResponseEntity.ok(playlists);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/playlists/{playlistId}/songs/{songId}")
    public ResponseEntity<?> addSongToPlaylist(@PathVariable Long playlistId, @PathVariable Long songId, HttpSession session) {
        String token = (String) session.getAttribute("JWT_TOKEN");
        if (token == null) return ResponseEntity.status(401).body("Unauthorized");
        
        try {
            userApiService.addSongToPlaylist(playlistId, songId, token);
            return ResponseEntity.ok("Song added to playlist");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
