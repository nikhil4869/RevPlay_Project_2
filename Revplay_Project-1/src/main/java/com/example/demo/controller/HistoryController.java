package com.example.demo.controller;

import com.example.demo.dto.music.HistoryDTO;
import com.example.demo.dto.music.ListeningTimeDTO;
import com.example.demo.dto.music.MostPlayedDTO;
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

    // last 50 songs
    @GetMapping("/recent")
    public List<HistoryDTO> getRecentHistory() {
        return historyService.getRecentHistory();
    }

    // full history
    @GetMapping
    public List<HistoryDTO> getFullHistory() {
        return historyService.getFullHistory();
    }

    // clear history
    @DeleteMapping
    public String clearHistory() {
        historyService.clearHistory();
        return "History cleared";
    }
    
    @GetMapping("/listening-time")
    public ListeningTimeDTO listeningTime() {
        return historyService.getListeningTime();
    }
    
    @GetMapping("/most-played")
    public List<MostPlayedDTO> mostPlayed() {
        return historyService.getMostPlayed();
    }
}