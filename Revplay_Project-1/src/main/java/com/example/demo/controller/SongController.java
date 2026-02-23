package com.example.demo.controller;

import com.example.demo.service.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.music.SongDTO;
import java.util.List;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    // upload song
    @PostMapping("/upload")
    public ResponseEntity<SongDTO> uploadSong(
            @RequestParam String title,
            @RequestParam String genre,
            @RequestParam String duration,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) Long albumId,
            @RequestParam(required = false) Integer trackNumber) {

        return ResponseEntity.ok(
                songService.uploadSong(title, genre, duration, file, albumId, trackNumber));
    }

    // get songs uploaded by artist
    @GetMapping("/my")
    public ResponseEntity<List<SongDTO>> getMySongs() {
        return ResponseEntity.ok(songService.getMySongs());
    }

    // Song cover
    @PostMapping("/{id}/cover")
    public ResponseEntity<SongDTO> uploadCover(
            @PathVariable Long id,
            @RequestParam MultipartFile image) {

        return ResponseEntity.ok(songService.uploadCover(id, image));
    }

    // add songs to album
    @PutMapping("/{songId}/album/{albumId}")
    public String addSongToAlbum(@PathVariable Long songId,
                                 @PathVariable Long albumId,
                                 @RequestParam Integer trackNumber) {

        songService.addSongToAlbum(songId, albumId, trackNumber);
        return "Song added to album";
    }

    // Get album by Id
    @GetMapping("/album/{albumId}")
    public List<SongDTO> getAlbumSongs(@PathVariable Long albumId) {
        return songService.getAlbumSongs(albumId);
    }

    // removing song from album not deleting song
    @PutMapping("/{songId}/remove-album")
    public String removeFromAlbum(@PathVariable Long songId) {
        songService.removeFromAlbum(songId);
        return "Song removed from album";
    }

    // re-ordering track numbers in album
    @PutMapping("/{songId}/reorder")
    public String reorderTrack(@PathVariable Long songId,
                               @RequestParam Integer newTrackNumber) {

        songService.reorderTrack(songId, newTrackNumber);
        return "Track reordered successfully";
    }

    @DeleteMapping("/{songId}")
    public String deleteSong(@PathVariable Long songId) {
        songService.deleteSong(songId);
        return "Song deleted permanently";
    }
    
 // get all songs (for listener)
    @GetMapping
    public ResponseEntity<List<SongDTO>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }
}