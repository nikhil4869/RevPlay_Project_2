package com.example.demo.controller;

import com.example.demo.dto.music.SongDTO;
import com.example.demo.service.HistoryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/history")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping("/{songId}")
    public String recordPlay(@PathVariable Long songId) {
        historyService.recordPlay(songId);
        return "Play recorded";
    }

    @GetMapping
    public List<SongDTO> myHistory() {
        return historyService.getMyHistory();
    }
    
    @GetMapping("/recent")
    public List<SongDTO> recentlyPlayed() {
        return historyService.getRecentlyPlayed();
    }
}