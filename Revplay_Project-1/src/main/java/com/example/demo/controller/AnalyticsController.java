package com.example.demo.controller;

import com.example.demo.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // Listener summary analytics
    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return analyticsService.getListenerSummary();
    }

    @GetMapping("/top-songs")
    public Object getTopSongs(@RequestParam(defaultValue = "5") int limit) {
        return analyticsService.getTopSongs(limit);
    }

    @GetMapping("/top-genres")
    public Object getTopGenres() {
        return analyticsService.getTopGenres();
    }
}