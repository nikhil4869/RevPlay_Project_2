package com.example.demo.controller;

<<<<<<< HEAD
import com.example.demo.dto.playlist.PlaylistDTO;
import com.example.demo.service.PlaylistService;

=======
import com.example.demo.dto.music.SongDTO;
import com.example.demo.dto.playlist.PlaylistDTO;
import com.example.demo.service.PlaylistService;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

<<<<<<< HEAD
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
=======
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
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    }

    @GetMapping("/public")
    public List<PlaylistDTO> publicPlaylists() {
<<<<<<< HEAD
        return playlistService.getPublicPlaylists();
    }
}
=======
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
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
