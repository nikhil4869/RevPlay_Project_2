package com.example.demo.controller;

import com.example.demo.service.AlbumService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.music.AlbumDTO;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    // create album
    @PostMapping
    public ResponseEntity<AlbumDTO> createAlbum(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate releaseDate) {

        return ResponseEntity.ok(
                albumService.createAlbum(name, description, releaseDate));
    }

    // upload album cover
    @PostMapping("/{id}/cover")
    public ResponseEntity<AlbumDTO> uploadCover(
            @PathVariable Long id,
            @RequestParam MultipartFile image) {

        return ResponseEntity.ok(albumService.uploadCover(id, image));
    }

    // get my albums
    @GetMapping("/my")
    public ResponseEntity<List<AlbumDTO>> getMyAlbums() {
        return ResponseEntity.ok(albumService.getMyAlbums());
    }
}