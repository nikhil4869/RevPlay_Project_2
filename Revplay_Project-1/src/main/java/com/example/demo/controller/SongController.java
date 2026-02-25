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
<<<<<<< HEAD
            @RequestParam(required = false) Integer trackNumber) {

        return ResponseEntity.ok(
                songService.uploadSong(title, genre, duration, file, albumId, trackNumber));
=======
            @RequestParam(required = false) Integer trackNumber,
            @RequestParam Integer releaseYear){

        return ResponseEntity.ok(
                songService.uploadSong(title, genre, duration, file, albumId, trackNumber,releaseYear));
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    }

    // get songs uploaded by artist
    @GetMapping("/my")
    public ResponseEntity<List<SongDTO>> getMySongs() {
        return ResponseEntity.ok(songService.getMySongs());
    }
<<<<<<< HEAD

    // Song cover
=======
    
    //Song cover
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    @PostMapping("/{id}/cover")
    public ResponseEntity<SongDTO> uploadCover(
            @PathVariable Long id,
            @RequestParam MultipartFile image) {

        return ResponseEntity.ok(songService.uploadCover(id, image));
    }
<<<<<<< HEAD

    // add songs to album
=======
    
    //add songs to album
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    @PutMapping("/{songId}/album/{albumId}")
    public String addSongToAlbum(@PathVariable Long songId,
                                 @PathVariable Long albumId,
                                 @RequestParam Integer trackNumber) {

        songService.addSongToAlbum(songId, albumId, trackNumber);
        return "Song added to album";
    }
<<<<<<< HEAD

    // Get album by Id
=======
    
    //Get album by Id
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    @GetMapping("/album/{albumId}")
    public List<SongDTO> getAlbumSongs(@PathVariable Long albumId) {
        return songService.getAlbumSongs(albumId);
    }

<<<<<<< HEAD
    // removing song from album not deleting song
=======
    //removing song from album not deleting song
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    @PutMapping("/{songId}/remove-album")
    public String removeFromAlbum(@PathVariable Long songId) {
        songService.removeFromAlbum(songId);
        return "Song removed from album";
    }

<<<<<<< HEAD
    // re-ordering track numbers in album
=======
    //re-ordering track numbers in album
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    @PutMapping("/{songId}/reorder")
    public String reorderTrack(@PathVariable Long songId,
                               @RequestParam Integer newTrackNumber) {

        songService.reorderTrack(songId, newTrackNumber);
        return "Track reordered successfully";
    }
<<<<<<< HEAD

=======
    
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    @DeleteMapping("/{songId}")
    public String deleteSong(@PathVariable Long songId) {
        songService.deleteSong(songId);
        return "Song deleted permanently";
    }
<<<<<<< HEAD
    
 // get all songs (for listener)
    @GetMapping
    public ResponseEntity<List<SongDTO>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }
}
=======

    //  browse all songs
    @GetMapping("/all")
    public List<SongDTO> getAllSongs() {
        return songService.getAllSongs();
    }

    //  song details
    @GetMapping("/public/{id}")
    public SongDTO getSongDetails(@PathVariable Long id) {
        return songService.getSongDetails(id);
    }

    @GetMapping("/public")
    public List<SongDTO> getPublicSongs() {
        return songService.getPublicSongs();
    }



}
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
