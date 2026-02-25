package com.example.demo.controller;

<<<<<<< HEAD
import com.example.demo.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
=======
import com.example.demo.dto.analytics.ArtistAnalyticsDTO;
import com.example.demo.dto.analytics.ListenerInsightsDTO;
import com.example.demo.dto.analytics.SongPlayChartDTO;
import com.example.demo.service.AnalyticsService;
import com.example.demo.dto.analytics.DailyTrendDTO;
import java.util.List;

import org.springframework.web.bind.annotation.*;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

<<<<<<< HEAD
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
=======
    @GetMapping("/artist")
    public ArtistAnalyticsDTO artistAnalytics() {
        return analyticsService.getArtistAnalytics();
    }
    
    @GetMapping("/plays-chart")
    public List<SongPlayChartDTO> playChart() {
        return analyticsService.getPlayChart();
    }
    
    @GetMapping("/listeners")
    public ListenerInsightsDTO listenerInsights() {
        return analyticsService.getListenerInsights();
    }
    


    @GetMapping("/trends")
    public List<DailyTrendDTO> getDailyTrends() {
        return analyticsService.getDailyTrends();
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    }
}