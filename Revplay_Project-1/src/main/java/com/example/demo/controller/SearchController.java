package com.example.demo.controller;
import org.springframework.web.bind.annotation.*;

<<<<<<< HEAD
import com.example.demo.dto.music.SongDTO;
import com.example.demo.service.SearchService;
import org.springframework.web.bind.annotation.*;
=======
import com.example.demo.dto.music.AlbumDTO;
import com.example.demo.dto.music.ArtistDTO;
import com.example.demo.dto.music.SongDTO;
import com.example.demo.service.SearchService;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

<<<<<<< HEAD
    @GetMapping("/title")
    public List<SongDTO> searchByTitle(@RequestParam String q) {
        return searchService.searchByTitle(q);
    }

    @GetMapping("/artist")
    public List<SongDTO> searchByArtist(@RequestParam String q) {
        return searchService.searchByArtist(q);
    }

    @GetMapping("/genre")
    public List<SongDTO> searchByGenre(@RequestParam String q) {
        return searchService.searchByGenre(q);
    }
}
=======
    @GetMapping("/songs")
    public List<SongDTO> searchSongs(@RequestParam String keyword) {
        return searchService.searchSongs(keyword);
    }

    @GetMapping("/artists")
    public List<ArtistDTO> searchArtists(@RequestParam String keyword) {
        return searchService.searchArtists(keyword);
    }

    @GetMapping("/albums")
    public List<AlbumDTO> searchAlbums(@RequestParam String keyword) {
        return searchService.searchAlbums(keyword);
    }
    
    @GetMapping("/year")
    public List<SongDTO> searchByYear(@RequestParam Integer year) {
        return searchService.searchByYear(year);
    }
    
    @GetMapping("/genres")
    public List<String> getGenres() {
        return searchService.getAllGenres();
    }
    
    @GetMapping("/songs/by-genre")
    public List<SongDTO> getSongsByGenre(@RequestParam String genre) {
        return searchService.searchSongsByGenre(genre);
    }


}

>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
