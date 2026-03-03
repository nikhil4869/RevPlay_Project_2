package com.example.demo.controller;

import com.example.demo.dto.analytics.ArtistAnalyticsDTO;
import com.example.demo.dto.analytics.ListenerInsightsDTO;
import com.example.demo.dto.analytics.SongPlayChartDTO;
import com.example.demo.service.AnalyticsService;
import com.example.demo.dto.analytics.DailyTrendDTO;
import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

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
    }
}