package com.example.demo.controller;

import com.example.demo.dto.playlist.PlaylistDTO;
import com.example.demo.dto.playlist.PlaylistRecordingDTO;
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

    // CREATE PLAYLIST
    @PostMapping
    public String create(
            @RequestParam String name,
            @RequestParam(defaultValue = "false") boolean isPublic) {

        playlistService.createPlaylist(name, isPublic);
        return "Playlist created successfully";
    }

    // UPDATE PLAYLIST
    @PutMapping("/{playlistId}")
    public String update(
            @PathVariable Long playlistId,
            @RequestParam String name,
            @RequestParam boolean isPublic) {

        playlistService.updatePlaylist(playlistId, name, isPublic);
        return "Playlist updated successfully";
    }

    // ADD SONG
    @PostMapping("/{playlistId}/songs/{songId}")
    public String addSong(
            @PathVariable Long playlistId,
            @PathVariable Long songId) {

        playlistService.addSong(playlistId, songId);
        return "Song added to playlist";
    }

    // REMOVE SONG
    @DeleteMapping("/{playlistId}/songs/{songId}")
    public String removeSong(
            @PathVariable Long playlistId,
            @PathVariable Long songId) {

        playlistService.removeSong(playlistId, songId);
        return "Song removed from playlist";
    }

    // REORDER SONG
//    @PutMapping("/{playlistId}/reorder/{songId}")
//    public String reorder(
//            @PathVariable Long playlistId,
//            @PathVariable Long songId,
//            @RequestParam int position) {
//
//        playlistService.reorderSong(playlistId, songId, position);
//        return "Playlist reordered successfully";
//    }
    
    //REORDER SONG
    @PostMapping("/{playlistId}/songs/{songId}/record")
    public String recordSongFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long songId) {

        playlistService.recordFromPlaylist(playlistId, songId);

        return "Song recorded successfully";
    }

    // MY PLAYLISTS
    @GetMapping("/my")
    public List<PlaylistDTO> myPlaylists() {
        return playlistService.getMyPlaylists();
    }

    // PUBLIC PLAYLISTS
    @GetMapping("/public")
    public List<PlaylistDTO> publicPlaylists() {
        return playlistService.getPublicPlaylists();
    }

    // FOLLOW
    @PostMapping("/{playlistId}/follow")
    public String follow(@PathVariable Long playlistId) {
        playlistService.followPlaylist(playlistId);
        return "Followed playlist";
    }

    // UNFOLLOW
    @DeleteMapping("/{playlistId}/follow")
    public String unfollow(@PathVariable Long playlistId) {
        playlistService.unfollowPlaylist(playlistId);
        return "Unfollowed playlist";
    }

    // DELETE PLAYLIST
    @DeleteMapping("/{playlistId}")
    public String delete(@PathVariable Long playlistId) {
        playlistService.deletePlaylist(playlistId);
        return "Playlist deleted successfully";
    }
    
    @GetMapping("/{playlistId}/recordings")
    public List<PlaylistRecordingDTO> getRecordings(
            @PathVariable Long playlistId) {

        return playlistService.getPlaylistRecordings(playlistId);
    }
    
    @DeleteMapping("/recordings/{recordingId}")
    public String deleteRecording(@PathVariable Long recordingId) {

        playlistService.deleteRecording(recordingId);
        return "Recording deleted successfully";
    }
}