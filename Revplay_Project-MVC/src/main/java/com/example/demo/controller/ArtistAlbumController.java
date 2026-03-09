package com.example.demo.controller;

import com.example.demo.dto.music.AlbumDTO;
import com.example.demo.dto.music.SongDTO;
import com.example.demo.service.ArtistApiService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class ArtistAlbumController {

    private final ArtistApiService artistApiService;

    public ArtistAlbumController(ArtistApiService artistApiService) {
        this.artistApiService = artistApiService;
    }

    /* ==========================
       ALBUM LIST
     ========================== */

    @GetMapping("/artist/albums")
    public String albums(HttpSession session, Model model) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        List<AlbumDTO> albums = artistApiService.getMyAlbums(token);
        model.addAttribute("albums", albums);

        return "artist/albums";
    }

    /* ==========================
       CREATE ALBUM
     ========================== */

    @GetMapping("/artist/albums/create")
    public String showCreatePage() {
        return "artist/create-album";
    }

    @PostMapping("/artist/albums/create")
    public String createAlbum(@RequestParam String name,
                              @RequestParam String description,
                              @RequestParam String releaseDate,
                              HttpSession session) {

        String token = (String) session.getAttribute("JWT_TOKEN");
        artistApiService.createAlbum(token, name, description, releaseDate);

        return "redirect:/artist/albums";
    }

    /* ==========================
       VIEW ALBUM DETAILS
     ========================== */

    @GetMapping("/artist/albums/{id}")
    public String viewAlbum(@PathVariable Long id,
                            HttpSession session,
                            Model model) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        AlbumDTO album = artistApiService.getAlbumById(token, id);
        List<SongDTO> songs = artistApiService.getSongsByAlbum(token, id);

        model.addAttribute("album", album);
        model.addAttribute("songs", songs);

        return "artist/album-details";
    }

    /* ==========================
       EDIT ALBUM
     ========================== */

    @GetMapping("/artist/albums/{id}/edit")
    public String editAlbum(@PathVariable Long id,
                            HttpSession session,
                            Model model) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        AlbumDTO album = artistApiService.getAlbumById(token, id);
        model.addAttribute("album", album);

        return "artist/edit-album";
    }

    @PostMapping("/artist/albums/{id}/update")
    public String updateAlbum(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam String description,
                              @RequestParam String releaseDate,
                              HttpSession session) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        artistApiService.updateAlbum(token, id, name, description, releaseDate);

        return "redirect:/artist/albums";
    }

    /* ==========================
       ALBUM COVER
     ========================== */

    @GetMapping("/artist/albums/{id}/cover")
    public String showCoverUpload(@PathVariable Long id, Model model) {
        model.addAttribute("albumId", id);
        return "artist/upload-cover";
    }

    @PostMapping("/artist/albums/{id}/cover")
    public String uploadCover(@PathVariable Long id,
                              @RequestParam MultipartFile image,
                              HttpSession session) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        artistApiService.uploadAlbumCover(token, id, image);

        return "redirect:/artist/albums";
    }

    /* ==========================
       DELETE ALBUM
     ========================== */

    @PostMapping("/artist/albums/{id}/delete")
    public String deleteAlbum(@PathVariable Long id,
                              HttpSession session) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        artistApiService.deleteAlbum(token, id);

        return "redirect:/artist/albums";
    }

    /* ==========================
       REMOVE SONG FROM ALBUM
     ========================== */

    @PostMapping("/artist/songs/{songId}/remove")
    public String removeSongFromAlbum(@PathVariable Long songId,
                                      @RequestParam Long albumId,
                                      HttpSession session) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        artistApiService.removeSongFromAlbum(token, songId);

        return "redirect:/artist/albums/" + albumId;
    }

    /* ==========================
       ADD EXISTING SONG TO ALBUM
     ========================== */

    @GetMapping("/artist/albums/{id}/add-existing")
    public String showAddExisting(@PathVariable Long id,
                                  HttpSession session,
                                  Model model) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        List<SongDTO> songs = artistApiService.getMySongs(token);

        model.addAttribute("songs", songs);
        model.addAttribute("albumId", id);

        return "artist/add-existing-to-album";
    }

    @PostMapping("/artist/albums/{albumId}/assign-song")
    public String assignSong(@PathVariable Long albumId,
                             @RequestParam Long songId,
                             @RequestParam Integer trackNumber,
                             HttpSession session,
                             Model model) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        try {
            artistApiService.assignSongToAlbum(token, songId, albumId, trackNumber);
        } catch (Exception e) {
            // Error handled by Global error display on album details
            return "redirect:/artist/albums/" + albumId + "?error=" + e.getMessage();
        }

        return "redirect:/artist/albums/" + albumId;
    }

    /* ==========================
       UPLOAD NEW SONG DIRECTLY TO ALBUM
     ========================== */

    @GetMapping("/artist/albums/{id}/upload")
    public String showUploadToAlbum(@PathVariable Long id,
                                    Model model) {

        model.addAttribute("albumId", id);
        return "artist/upload-song-to-album";
    }

    @PostMapping("/artist/albums/{albumId}/upload")
    public String uploadSongToAlbum(@PathVariable Long albumId,
                                    @RequestParam String title,
                                    @RequestParam String genre,
                                    @RequestParam String duration,
                                    @RequestParam Integer releaseYear,
                                    @RequestParam MultipartFile file,
                                    HttpSession session) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        artistApiService.uploadSongToAlbum(token,
                title, genre, duration, releaseYear, file, albumId);

        return "redirect:/artist/albums/" + albumId;
    }
}