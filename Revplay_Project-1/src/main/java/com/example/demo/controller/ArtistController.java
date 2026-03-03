package com.example.demo.controller;

import com.example.demo.dto.music.ArtistDTO;
import com.example.demo.service.ArtistService;
import com.example.demo.service.SongService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/artist")
public class ArtistController {

    private final ArtistService artistService;
    private final SongService songService;



    public ArtistController(ArtistService artistService, SongService songService) {
		this.artistService = artistService;
		this.songService = songService;
	}

	// create artist profile
    @PostMapping("/profile")
    public ResponseEntity<ArtistDTO> createProfile(@RequestBody ArtistDTO dto) {
        return ResponseEntity.ok(artistService.createProfile(dto));
    }
    
    //Image of artist
    @PostMapping("/profile/{id}/image")
    public ResponseEntity<ArtistDTO> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam MultipartFile image) {

        return ResponseEntity.ok(artistService.uploadProfileImage(id, image));
    }

    //banner for artist profile
    @PostMapping("/profile/{id}/banner")
    public ResponseEntity<ArtistDTO> uploadBannerImage(
            @PathVariable Long id,
            @RequestParam MultipartFile image) {

        return ResponseEntity.ok(artistService.uploadBannerImage(id, image));
    }


    // update artist profile
    @PutMapping("/profile")
    public ResponseEntity<ArtistDTO> updateProfile(@RequestBody ArtistDTO dto) {
        return ResponseEntity.ok(artistService.updateProfile(dto));
    }

    // get my profile (artist dashboard)
    @GetMapping("/profile")
    public ResponseEntity<ArtistDTO> getMyProfile() {
        return ResponseEntity.ok(artistService.getMyProfile());
    }

    // public view of artist profile
    @GetMapping("/{id}")
    public ResponseEntity<ArtistDTO> getArtistProfile(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.getArtistProfile(id));
    }
    
    //View songs favourated
    @GetMapping("/song-favorites")
    public ResponseEntity<?> getFavoriteStats() {
        return ResponseEntity.ok(songService.getFavoriteStatsForMySongs());
    }

}
