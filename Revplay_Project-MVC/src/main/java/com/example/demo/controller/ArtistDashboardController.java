package com.example.demo.controller;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.service.ArtistApiService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class ArtistDashboardController {

    private final ArtistApiService artistApiService;

    public ArtistDashboardController(ArtistApiService artistApiService) {
        this.artistApiService = artistApiService;
    }

    /* ==========================
       ARTIST HOME
     ========================== */

    @GetMapping("/artist/home")
    public String artistHome() {
        return "artist/dashboard";
    }

    /* ==========================
       MY SONGS LIST
     ========================== */

    @GetMapping("/artist/songs")
    public String mySongs(HttpSession session, Model model) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        List<SongDTO> songs = artistApiService.getMySongs(token);
        model.addAttribute("songs", songs);

        return "artist/my-songs";
    }

    /* ==========================
       DELETE SONG
     ========================== */

    @PostMapping("/artist/songs/{id}/delete")
    public String deleteSong(@PathVariable Long id,
                             HttpSession session) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        artistApiService.deleteSong(id, token);

        return "redirect:/artist/songs";
    }

    /* ==========================
       UPLOAD SONG COVER
     ========================== */

    @GetMapping("/artist/songs/{id}/cover")
    public String showUploadSongCover(@PathVariable Long id,
                                      Model model) {

        model.addAttribute("songId", id);
        return "artist/upload-song-cover";
    }

    @PostMapping("/artist/songs/{id}/cover")
    public String uploadSongCover(@PathVariable Long id,
                                  @RequestParam("image") MultipartFile image,
                                  HttpSession session) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        artistApiService.uploadCover(id, image, token);

        return "redirect:/artist/songs";
    }
    
    /* ==========================
    UPLOAD SONG (DIRECT TO ARTIST)
  ========================== */

 @GetMapping("/artist/songs/upload")
 public String showUploadSongPage() {
     return "artist/upload-song";
 }

 @PostMapping("/artist/songs/upload")
 public String uploadSong(
         @RequestParam String title,
         @RequestParam String genre,
         @RequestParam String duration,
         @RequestParam Integer releaseYear,
         @RequestParam("file") MultipartFile file,
         HttpSession session,
         Model model) {

     String token = (String) session.getAttribute("JWT_TOKEN");

     try {
         artistApiService.uploadSong(
                 token,
                 title,
                 genre,
                 duration,
                 releaseYear,
                 file
         );

         return "redirect:/artist/songs";

     } catch (Exception e) {
         model.addAttribute("errorMessage", e.getMessage());
         return "artist/upload-song";
     }
 }


}