package com.example.demo.controller;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.service.SearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

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
    
    @GetMapping("/year")
    public List<SongDTO> searchByYear(@RequestParam Integer year) {
        return searchService.searchByYear(year);
    }

    @GetMapping("/album")
    public List<SongDTO> searchByAlbum(@RequestParam String name) {
        return searchService.searchByAlbum(name);
    }
}