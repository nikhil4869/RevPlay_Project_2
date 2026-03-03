package com.example.demo.controller;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.dto.playlist.PlaylistDTO;
import com.example.demo.service.PlaylistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistService service;

    public PlaylistController(PlaylistService service) {
        this.service = service;
    }

    @PostMapping
    public PlaylistDTO create(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam boolean isPublic) {

        return service.createPlaylist(name, description, isPublic);
    }

    @GetMapping("/my")
    public List<PlaylistDTO> myPlaylists() {
        return service.getMyPlaylists();
    }

    @GetMapping("/public")
    public List<PlaylistDTO> publicPlaylists() {
        return service.getPublicPlaylists();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.deletePlaylist(id);
        return "Playlist deleted";
    }
    
    @PutMapping("/{playlistId}/songs/{songId}")
    public String addSong(@PathVariable Long playlistId,
                          @PathVariable Long songId) {

    	service.addSongToPlaylist(playlistId, songId);
        return "Song added";
    }
    
    @DeleteMapping("/{playlistId}/songs/{songId}")
    public String removeSong(@PathVariable Long playlistId,
                             @PathVariable Long songId) {

        service.removeSongFromPlaylist(playlistId, songId);
        return "Song removed";
    }
    
    @GetMapping("/{playlistId}/songs")
    public List<SongDTO> getSongs(@PathVariable Long playlistId) {
        return service.getPlaylistSongs(playlistId);
    }
    
    @PostMapping("/{id}/follow")
    public String followPlaylist(@PathVariable Long id) {
        service.followPlaylist(id);
        return "Playlist followed";
    }
    
    @DeleteMapping("/{id}/unfollow")
    public String unfollowPlaylist(@PathVariable Long id) {
        service.unfollowPlaylist(id);
        return "Playlist unfollowed";
    }
    
    @GetMapping("/followed")
    public List<PlaylistDTO> getFollowedPlaylists() {
        return service.getFollowedPlaylists();
    }
    
}
