package com.example.demo.controller;

import com.example.demo.dto.playlist.PlaylistDTO;
import com.example.demo.service.PlaylistService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping
    public PlaylistDTO create(@RequestParam String name) {
        return playlistService.createPlaylist(name);
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public String addSong(@PathVariable Long playlistId,
                          @PathVariable Long songId) {
        playlistService.addSongToPlaylist(playlistId, songId);
        return "Song added to playlist";
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public String removeSong(@PathVariable Long playlistId,
                             @PathVariable Long songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);
        return "Song removed from playlist";
    }

    @GetMapping
    public List<PlaylistDTO> myPlaylists() {
        return playlistService.getMyPlaylists();
    }

    @GetMapping("/{playlistId}")
    public PlaylistDTO getPlaylist(@PathVariable Long playlistId) {
        return playlistService.getPlaylistWithSongs(playlistId);
    }
    
    @PutMapping("/{playlistId}/visibility")
    public PlaylistDTO changeVisibility(@PathVariable Long playlistId,
                                        @RequestParam boolean isPublic) {
        return playlistService.changeVisibility(playlistId, isPublic);
    }

    @GetMapping("/public")
    public List<PlaylistDTO> publicPlaylists() {
        return playlistService.getPublicPlaylists();
    }
}